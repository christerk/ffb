package com.fumbbl.ffb.server.step.bb2020.ttm;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.RightStuffModifierFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.RightStuffContext;
import com.fumbbl.ffb.modifiers.RightStuffModifier;
import com.fumbbl.ffb.report.ReportRightStuffRoll;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeFumbledKtm;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeTTMLanding;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;

import java.util.Arrays;
import java.util.Set;

/**
 * Step in ttm sequence to handle skill RIGHT_STUFF (landing roll).
 * <p>
 * Expects stepParameter DROP_THROWN_PLAYER to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_HAS_BALL to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 * <p>
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepRightStuff extends AbstractStepWithReRoll {

	private Boolean fThrownPlayerHasBall;
	private String fThrownPlayerId;
	private boolean fDropThrownPlayer, kickedPlayer;
	private PassResult passResult;
	private String goToOnSuccess;
	private PlayerState oldPlayerState;

	public StepRightStuff(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.RIGHT_STUFF;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			Arrays.stream(parameterSet.values()).forEach(parameter -> {
				switch (parameter.getKey()) {
					case GOTO_LABEL_ON_SUCCESS:
						goToOnSuccess = (String) parameter.getValue();
						break;
					case IS_KICKED_PLAYER:
						kickedPlayer = parameter.getValue() != null && (boolean) parameter.getValue();
						break;
					default:
						break;
				}
			});
		}
		super.init(parameterSet);
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case THROWN_PLAYER_HAS_BALL:
					fThrownPlayerHasBall = (Boolean) parameter.getValue();
					return true;
				case THROWN_PLAYER_ID:
					fThrownPlayerId = (String) parameter.getValue();
					return true;
				case DROP_THROWN_PLAYER:
					fDropThrownPlayer = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					return true;
				case PASS_RESULT:
					passResult = (PassResult) parameter.getValue();
					return true;
				case OLD_DEFENDER_STATE:
					oldPlayerState = (PlayerState) parameter.getValue();
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
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(thrownPlayer);
		// skip right stuff step when player has been thrown out of bounds or fell down a trapdoor
		if ((thrownPlayer != null) && (game.getFieldModel().getPlayerState(thrownPlayer).getBase() == PlayerState.FALLING || playerCoordinate.isBoxCoordinate())) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, fThrownPlayerHasBall));
			publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		game.getFieldModel().setPlayerState(thrownPlayer, oldPlayerState);
		publishParameter(StepParameter.from(StepParameterKey.THROWN_PLAYER_STATE, oldPlayerState));
		if (fThrownPlayerHasBall) {
			game.getFieldModel().setBallCoordinate(game.getFieldModel().getPlayerCoordinate(thrownPlayer));
		}
		boolean fumbledKtm = PassResult.FUMBLE == passResult && kickedPlayer;
		boolean doRoll = !fDropThrownPlayer && !fumbledKtm;
		if (doRoll && (ReRolledActions.RIGHT_STUFF == getReRolledAction())) {
			if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), thrownPlayer)) {
				doRoll = false;
			}
		}
		if (doRoll) {
			RightStuffModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.RIGHT_STUFF_MODIFIER);
			Set<RightStuffModifier> rightStuffModifiers = modifierFactory.findModifiers(new RightStuffContext(game, thrownPlayer, passResult));
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
			int minimumRoll = mechanic.minimumRollRightStuff(thrownPlayer, rightStuffModifiers);
			int roll = getGameState().getDiceRoller().rollSkill();
			boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
			boolean reRolled = ((getReRolledAction() == ReRolledActions.RIGHT_STUFF) && (getReRollSource() != null));

			if (PassResult.FUMBLE == passResult && game.getThrower() != null && game.getThrower().hasSkillProperty(NamedProperties.fumbledPlayerLandsSafely)) {
				successful = true;
				getResult().addReport(new ReportSkillUse(game.getThrowerId(),
					game.getThrower().getSkillWithProperty(NamedProperties.fumbledPlayerLandsSafely),
					true, SkillUse.FUMBLED_PLAYER_LANDS_SAFELY));
			} else {
				getResult().addReport(new ReportRightStuffRoll(fThrownPlayerId, successful, roll,
					minimumRoll, reRolled, rightStuffModifiers.toArray(new RightStuffModifier[0])));
			}
			if (successful) {
				if (passResult == PassResult.ACCURATE && !kickedPlayer) {
					GameResult gameResult = getGameState().getGame().getGameResult();
					TeamResult teamResult = game.getActingTeam() == game.getTeamHome() ? gameResult.getTeamResultHome() : gameResult.getTeamResultAway();
					if (game.getThrower() != null) {
						getGameState().getPrayerState().addCompletion(teamResult.getPlayerResult(game.getThrower()));
					}
				}
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
				getResult().setNextAction(StepAction.GOTO_LABEL, goToOnSuccess);
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
			InjuryResult injuryResultThrownPlayer = UtilServerInjury.handleInjury(this, fumbledKtm ? new InjuryTypeFumbledKtm() : new InjuryTypeTTMLanding(),
				game.getActingPlayer().getPlayer(), thrownPlayer, playerCoordinate, null, null, ApothecaryMode.THROWN_PLAYER);
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
		IServerJsonOption.PASS_RESULT.addTo(jsonObject, passResult);
		IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, goToOnSuccess);
		IServerJsonOption.IS_KICKED_PLAYER.addTo(jsonObject, kickedPlayer);
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, oldPlayerState);
		return jsonObject;
	}

	@Override
	public StepRightStuff initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fThrownPlayerHasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(source, jsonObject);
		fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		passResult = (PassResult) IServerJsonOption.PASS_RESULT.getFrom(source, jsonObject);
		goToOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(source, jsonObject);
		kickedPlayer = IServerJsonOption.IS_KICKED_PLAYER.getFrom(source, jsonObject);
		fDropThrownPlayer = IServerJsonOption.DROP_THROWN_PLAYER.getFrom(source, jsonObject);
		oldPlayerState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		return this;
	}

}
