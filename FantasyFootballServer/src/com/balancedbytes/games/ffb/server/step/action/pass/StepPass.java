package com.balancedbytes.games.ffb.server.step.action.pass;

import java.util.Set;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.PassModifierFactory;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportPassRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPassing;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in the pass sequence to handle passing the ball.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_MISSED_PASS.
 * 
 * Expects stepParameter CATCHER_ID to be set by a preceding step.
 * 
 * Sets stepParameter CATCHER_ID for all steps on the stack. Sets stepParameter
 * PASS_ACCURATE for all steps on the stack. Sets stepParameter PASS_FUMBLE for
 * all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepPass extends AbstractStepWithReRoll {

	public static class StepState {
		public String goToLabelOnEnd;
		public String goToLabelOnMissedPass;
		public String CatcherId;
		public boolean successful;
		public boolean holdingSafeThrow;
		public boolean passFumble;
		public boolean passSkillUsed;
	}

	private StepState state;

	public StepPass(GameState pGameState) {
		super(pGameState);

		state = new StepState();
	}

	public StepId getId() {
		return StepId.PASS;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_END:
					state.goToLabelOnEnd = (String) parameter.getValue();
					break;
				// mandatory
				case GOTO_LABEL_ON_MISSED_PASS:
					state.goToLabelOnMissedPass = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (!StringTool.isProvided(state.goToLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
		if (!StringTool.isProvided(state.goToLabelOnMissedPass)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_MISSED_PASS + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			if (pParameter.getKey() == StepParameterKey.CATCHER_ID) {
				state.CatcherId = (String) pParameter.getValue();
				return true;
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
			commandStatus = handleSkillCommand((ClientCommandUseSkill) pReceivedCommand.getCommand(), state);
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		if ((game.getThrower() == null) || (game.getThrowerAction() == null)) {
			return;
		}
		if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
			game.getFieldModel().setBombMoving(true);
		} else {
			game.getFieldModel().setBallMoving(true);
		}
		if (ReRolledActions.PASS == getReRolledAction()) {
			if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), game.getThrower())) {
				handleFailedPass();
				return;
			}
		}
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
		PassingDistance passingDistance = UtilPassing.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(),
				false);
		Set<PassModifier> passModifiers = new PassModifierFactory().findPassModifiers(game, game.getThrower(),
				passingDistance, false);
		int minimumRoll = DiceInterpreter.getInstance().minimumRollPass(game.getThrower(), passingDistance, passModifiers);
		int roll = getGameState().getDiceRoller().rollSkill();
		if (roll == 6) {
			state.successful = true;
			state.passFumble = false;
			state.holdingSafeThrow = false;
		} else if (roll == 1) {
			state.successful = false;
			state.passFumble = true;
			state.holdingSafeThrow = false;
		} else {
			state.passFumble = DiceInterpreter.getInstance().isPassFumble(roll, game.getThrower(), passingDistance,
					passModifiers);
			if (state.passFumble) {
				state.successful = false;
				state.holdingSafeThrow = (game.getThrower().hasSkillWithProperty(NamedProperties.dontDropFumbles) && (PlayerAction.THROW_BOMB != game.getThrowerAction()));
				publishParameter(new StepParameter(StepParameterKey.DONT_DROP_FUMBLE, state.holdingSafeThrow));
			} else {
				state.successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
				state.holdingSafeThrow = false;
			}
		}
		PassModifier[] passModifierArray = new PassModifierFactory().toArray(passModifiers);
		boolean reRolled = ((getReRolledAction() == ReRolledActions.PASS) && (getReRollSource() != null));
		getResult().addReport(new ReportPassRoll(game.getThrowerId(), state.successful, roll, minimumRoll, reRolled,
				passModifierArray, passingDistance, state.passFumble, state.holdingSafeThrow,
				(PlayerAction.THROW_BOMB == game.getThrowerAction())));
		if (state.successful) {
			game.getFieldModel().setRangeRuler(null);
			publishParameter(new StepParameter(StepParameterKey.PASS_FUMBLE, state.passFumble));
			FieldCoordinate startCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
			if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
				getResult()
						.setAnimation(new Animation(AnimationType.THROW_BOMB, startCoordinate, game.getPassCoordinate(), null));
			} else {
				getResult().setAnimation(new Animation(AnimationType.PASS, startCoordinate, game.getPassCoordinate(), null));
			}
			UtilServerGame.syncGameModel(this);
			Player<?> catcher = game.getPlayerById(state.CatcherId);
			PlayerState catcherState = game.getFieldModel().getPlayerState(catcher);
			if ((catcher == null) || (catcherState == null) || !catcherState.hasTacklezones()) {
				if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
					game.getFieldModel().setBombCoordinate(game.getPassCoordinate());
					publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE,
							state.CatcherId == null ? CatchScatterThrowInMode.CATCH_ACCURATE_BOMB_EMPTY_SQUARE
									: CatchScatterThrowInMode.CATCH_BOMB));
				} else {
					game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
					publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE,
							state.CatcherId == null ? CatchScatterThrowInMode.CATCH_ACCURATE_PASS_EMPTY_SQUARE
									: CatchScatterThrowInMode.CATCH_MISSED_PASS));
				}
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
					game.getFieldModel().setBombCoordinate(game.getPassCoordinate());
					publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE,
							CatchScatterThrowInMode.CATCH_ACCURATE_BOMB));
				} else {
					game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
					publishParameter(new StepParameter(StepParameterKey.PASS_ACCURATE, true));
					publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE,
							CatchScatterThrowInMode.CATCH_ACCURATE_PASS));
				}
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		} else {
			boolean doNextStep = true;
			if (getReRolledAction() != ReRolledActions.PASS) {
				setReRolledAction(ReRolledActions.PASS);

				ReRollSource PassingReroll = UtilCards.getRerollSource(game.getThrower(), ReRolledActions.PASS);
				if (PassingReroll != null && !state.passSkillUsed) {
					doNextStep = false;
					state.passSkillUsed = true;
					Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
					UtilServerDialog.showDialog(getGameState(),
							new DialogSkillUseParameter(game.getThrowerId(), PassingReroll.getSkill(game), minimumRoll),
							actingTeam.hasPlayer(game.getThrower()));
				} else {
					if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), game.getThrower(), ReRolledActions.PASS,
							minimumRoll, state.passFumble)) {
						doNextStep = false;
					}
				}
			}
			if (doNextStep) {
				handleFailedPass();
			}
		}
	}

	private void handleFailedPass() {
		Game game = getGameState().getGame();
		game.getFieldModel().setRangeRuler(null);
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
		publishParameter(new StepParameter(StepParameterKey.PASS_FUMBLE, state.passFumble));
		if (state.holdingSafeThrow) {
			game.getFieldModel().setBallCoordinate(throwerCoordinate);
			game.getFieldModel().setBallMoving(false);
			getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnEnd);
		} else if (state.passFumble) {
			if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
				game.getFieldModel().setBombCoordinate(game.getFieldModel().getPlayerCoordinate(game.getThrower()));
			} else {
				game.getFieldModel().setBallCoordinate(game.getFieldModel().getPlayerCoordinate(game.getThrower()));
				publishParameter(
						new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
			}
			publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, null));
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
				game.getFieldModel().setBombCoordinate(game.getPassCoordinate());
			} else {
				game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
			}
			publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, null));
			getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnMissedPass);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, state.goToLabelOnEnd);
		IServerJsonOption.GOTO_LABEL_ON_MISSED_PASS.addTo(jsonObject, state.goToLabelOnMissedPass);
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, state.CatcherId);
		IServerJsonOption.SUCCESSFUL.addTo(jsonObject, state.successful);
		IServerJsonOption.HOLDING_SAFE_THROW.addTo(jsonObject, state.holdingSafeThrow);
		IServerJsonOption.PASS_FUMBLE.addTo(jsonObject, state.passFumble);
		IServerJsonOption.PASS_SKILL_USED.addTo(jsonObject, state.passSkillUsed);
		return jsonObject;
	}

	@Override
	public StepPass initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state.goToLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(game, jsonObject);
		state.goToLabelOnMissedPass = IServerJsonOption.GOTO_LABEL_ON_MISSED_PASS.getFrom(game, jsonObject);
		state.CatcherId = IServerJsonOption.CATCHER_ID.getFrom(game, jsonObject);
		state.successful = IServerJsonOption.SUCCESSFUL.getFrom(game, jsonObject);
		state.holdingSafeThrow = IServerJsonOption.HOLDING_SAFE_THROW.getFrom(game, jsonObject);
		state.passFumble = IServerJsonOption.PASS_FUMBLE.getFrom(game, jsonObject);
		state.passSkillUsed = IServerJsonOption.PASS_SKILL_USED.getFrom(game, jsonObject);
		return this;
	}

}
