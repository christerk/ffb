package com.balancedbytes.games.ffb.server.step.action.ttm;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.RightStuffModifierFactory;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.mechanics.AgilityMechanic;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.KickTeamMateRange;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.modifiers.RightStuffContext;
import com.balancedbytes.games.ffb.modifiers.RightStuffModifier;
import com.balancedbytes.games.ffb.report.ReportRightStuffRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeTTMLanding;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Set;

/**
 * Step in ttm sequence to handle skill RIGHT_STUFF (landing roll).
 *
 * Expects stepParameter DROP_THROWN_PLAYER to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_HAS_BALL to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 *
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepRightStuff extends AbstractStepWithReRoll {

	private Boolean fThrownPlayerHasBall;
	private String fThrownPlayerId;
	private boolean fDropThrownPlayer;
	private KickTeamMateRange ktmRange;

	public StepRightStuff(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.RIGHT_STUFF;
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case KICKED_PLAYER_HAS_BALL:
			case THROWN_PLAYER_HAS_BALL:
				fThrownPlayerHasBall = (Boolean) pParameter.getValue();
				return true;
			case KICKED_PLAYER_ID:
			case THROWN_PLAYER_ID:
				fThrownPlayerId = (String) pParameter.getValue();
				return true;
			case DROP_THROWN_PLAYER:
				fDropThrownPlayer = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				return true;
			case KTM_MODIFIER:
				ktmRange = (pParameter.getValue() != null) ? (KickTeamMateRange) pParameter.getValue() : KickTeamMateRange.SHORT;
				return true;
			default:
				break;
			}
		}
		return false;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		Player<?> thrownPlayer = game.getPlayerById(fThrownPlayerId);
		// skip right stuff step when player has been thrown out of bounds
		if ((thrownPlayer != null) && game.getFieldModel().getPlayerState(thrownPlayer).getBase() == PlayerState.FALLING) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, fThrownPlayerHasBall));
			publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		if (fThrownPlayerHasBall) {
			game.getFieldModel().setBallCoordinate(game.getFieldModel().getPlayerCoordinate(thrownPlayer));
		}
		boolean doRoll = true;
		if (fDropThrownPlayer) {
			doRoll = false;
		}
		if (!fDropThrownPlayer && (ReRolledActions.RIGHT_STUFF == getReRolledAction())) {
			if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), thrownPlayer)) {
				doRoll = false;
			}
		}
		if (doRoll) {
			RightStuffModifierFactory modifierFactory = new RightStuffModifierFactory();
			Set<RightStuffModifier> rightStuffModifiers = modifierFactory.findModifiers(new RightStuffContext(game, thrownPlayer, ktmRange));
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
			int minimumRoll = mechanic.minimumRollRightStuff(thrownPlayer, rightStuffModifiers);
			int roll = getGameState().getDiceRoller().rollSkill();
			boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
			boolean reRolled = ((getReRolledAction() == ReRolledActions.RIGHT_STUFF) && (getReRollSource() != null));
			getResult().addReport(new ReportRightStuffRoll(fThrownPlayerId, successful, roll,
					minimumRoll, reRolled, rightStuffModifiers.toArray(new RightStuffModifier[0])));
			if (successful) {
				if (fThrownPlayerHasBall) {
					if (UtilServerSteps.checkTouchdown(getGameState())) {
						publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
					}
				} else {
					if (game.getFieldModel().getPlayerCoordinate(thrownPlayer).equals(game.getFieldModel().getBallCoordinate())) {
						game.getFieldModel().setBallMoving(true);
						publishParameter(
								new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
					}
				}
				publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				if (getReRolledAction() != ReRolledActions.RIGHT_STUFF) {
					setReRolledAction(ReRolledActions.RIGHT_STUFF);
					doRoll = UtilServerReRoll.askForReRollIfAvailable(getGameState(), thrownPlayer, ReRolledActions.RIGHT_STUFF,
							minimumRoll, false);
				} else {
					doRoll = false;
				}
			}
		}
		if (!doRoll) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(thrownPlayer);
			InjuryResult injuryResultThrownPlayer = UtilServerInjury.handleInjury(this, new InjuryTypeTTMLanding(), null,
					thrownPlayer, playerCoordinate, null, ApothecaryMode.THROWN_PLAYER);
			publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultThrownPlayer));
			StepParameterSet params = UtilServerInjury.dropPlayer(this, thrownPlayer, ApothecaryMode.THROWN_PLAYER);
			if (!fThrownPlayerHasBall) {
				params.remove(StepParameterKey.END_TURN);
			}
			publishParameters(params);
			if (fThrownPlayerHasBall) {
				publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			}
			publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.THROWN_PLAYER_HAS_BALL.addTo(jsonObject, fThrownPlayerHasBall);
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
		IServerJsonOption.DROP_THROWN_PLAYER.addTo(jsonObject, fDropThrownPlayer);
		IServerJsonOption.KICK_TEAM_MATE_RANGE.addTo(jsonObject, ktmRange.name());
		return jsonObject;
	}

	@Override
	public StepRightStuff initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fThrownPlayerHasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(game, jsonObject);
		fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(game, jsonObject);
		String storedKtmRange = IServerJsonOption.KICK_TEAM_MATE_RANGE.getFrom(game, jsonObject);
		if (storedKtmRange != null) {
			ktmRange = KickTeamMateRange.valueOf(storedKtmRange);
		}
		return this;
	}

}
