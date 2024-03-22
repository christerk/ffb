package com.fumbbl.ffb.server.step.bb2020.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.GoForItModifierFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.GoForItContext;
import com.fumbbl.ffb.modifiers.GoForItModifier;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportGoForItRoll;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeDropGFI;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Set;

/**
 * Step in block sequence to handle rush blitz.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * <p>
 * Sets stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * INJURY_TYPE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepGoForIt extends AbstractStepWithReRoll {
	private boolean fBallandChainGfi;
	private boolean fSecondGoForIt;
	private String fGotoLabelOnFailure;
	private FieldCoordinate moveStart;
	private Boolean usingModifierIgnoringSkill;
	int roll;

	public StepGoForIt(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.GO_FOR_IT;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					case GOTO_LABEL_ON_FAILURE:
						fGotoLabelOnFailure = (String) parameter.getValue();
						break;
					case BALL_AND_CHAIN_GFI:
						fBallandChainGfi = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
						break;
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnFailure)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.MOVE_START) {
			moveStart = (FieldCoordinate) parameter.getValue();
			return true;
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
			ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
			if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canMakeUnmodifiedRush)) {
				usingModifierIgnoringSkill = commandUseSkill.isSkillUsed();
				if (!usingModifierIgnoringSkill) {
					setReRollSource(findSkillReRollSource(ReRolledActions.RUSH));
				}
				commandStatus = StepCommandStatus.EXECUTE_STEP;
			} else {
				commandStatus = handleSkillCommand((ClientCommandUseSkill) pReceivedCommand.getCommand(), getGameState().getPassState());
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		boolean goForItAfterBlock = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.goForItAfterBlock);
		boolean runGfi = (goForItAfterBlock == fBallandChainGfi);

		if (runGfi) {
			if ((PlayerAction.BLITZ == actingPlayer.getPlayerAction()) && (getReRolledAction() == null)) {
				game.getTurnData().setBlitzUsed(true);
				actingPlayer.setCurrentMove(actingPlayer.getCurrentMove() + 1);
				actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game));
			}
			if (actingPlayer.isGoingForIt()
				&& (actingPlayer.getCurrentMove() > actingPlayer.getPlayer().getMovementWithModifiers())) {
				if (ReRolledActions.RUSH == getReRolledAction() && (usingModifierIgnoringSkill == null || !usingModifierIgnoringSkill)) {
					if ((getReRollSource() == null)
						|| !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
						if ( !Boolean.TRUE.equals(usingModifierIgnoringSkill)) {
							failGfi();
						}
						return;
					}
				}
				switch (rush()) {
					case SUCCESS:
						succeedGfi();
						return;
					case FAILURE:
						failGfi();
						return;
					default:
						getResult().setNextAction(StepAction.CONTINUE);
						return;
				}
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void succeedGfi() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.isJumping()
			&& (actingPlayer.getCurrentMove() > actingPlayer.getPlayer().getMovementWithModifiers() + 1)
			&& !fSecondGoForIt) {
			fSecondGoForIt = true;
			setReRolledAction(null);
			getGameState().pushCurrentStepOnStack();
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void failGfi() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.isJumping() && !fSecondGoForIt && actingPlayer.getCurrentMove() > actingPlayer.getPlayer().getMovementWithModifiers() + 1) {
			publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, null));
			game.getFieldModel().updatePlayerAndBallPosition(actingPlayer.getPlayer(), moveStart);
		}

		publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
		publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, new InjuryTypeDropGFI()));


		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
	}

	private ActionStatus rush() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		GoForItModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.GO_FOR_IT_MODIFIER);
		Set<GoForItModifier> goForItModifiers = modifierFactory.findModifiers(new GoForItContext(game, actingPlayer.getPlayer(), getGameState().getPrayerState().getMolesUnderThePitch()));
		int minimumRoll = DiceInterpreter.getInstance().minimumRollGoingForIt(goForItModifiers);

		if (roll == 0 && (usingModifierIgnoringSkill == null || !usingModifierIgnoringSkill)) {
			roll = getGameState().getDiceRoller().rollGoingForIt();
		}

		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
		boolean reRolled = ((getReRolledAction() == ReRolledActions.RUSH) && (getReRollSource() != null));
		getResult().addReport(new ReportGoForItRoll(actingPlayer.getPlayerId(), successful, roll,
			minimumRoll, reRolled, goForItModifiers.toArray(new GoForItModifier[0])));
		if (successful) {
			return ActionStatus.SUCCESS;
		} else {
			if (getReRolledAction() != ReRolledActions.RUSH) {
				setReRolledAction(ReRolledActions.RUSH);
				ReRollSource gfiRerollSource = UtilCards.getUnusedRerollSource(actingPlayer, ReRolledActions.RUSH);

				if (gfiRerollSource != null && TurnMode.REGULAR == game.getTurnMode()) {
					setReRollSource(gfiRerollSource);
					UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer());
					return rush();
				} else {
					if (!reRolled && UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(),
						ReRolledActions.RUSH, minimumRoll, false)) {
						return ActionStatus.WAITING_FOR_RE_ROLL;
					} else {
						return ActionStatus.FAILURE;
					}
				}
			} else {
				return ActionStatus.FAILURE;
			}
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.SECOND_GO_FOR_IT.addTo(jsonObject, fSecondGoForIt);
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		IServerJsonOption.MOVE_START.addTo(jsonObject, moveStart);
		IServerJsonOption.USING_MODIFIER_IGNORING_SKILL.addTo(jsonObject, usingModifierIgnoringSkill);
		IServerJsonOption.ROLL.addTo(jsonObject, roll);
		return jsonObject;
	}

	@Override
	public StepGoForIt initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fSecondGoForIt = IServerJsonOption.SECOND_GO_FOR_IT.getFrom(source, jsonObject);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		moveStart = IServerJsonOption.MOVE_START.getFrom(source, jsonObject);
		usingModifierIgnoringSkill = IServerJsonOption.USING_MODIFIER_IGNORING_SKILL.getFrom(source, jsonObject);
		roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		return this;
	}

}
