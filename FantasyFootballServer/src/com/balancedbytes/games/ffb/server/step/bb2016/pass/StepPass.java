package com.balancedbytes.games.ffb.server.step.bb2016.pass;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.PassModifierFactory;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.mechanics.PassMechanic;
import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.modifiers.PassContext;
import com.balancedbytes.games.ffb.modifiers.PassModifier;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportPassRoll;
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
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Optional;
import java.util.Set;

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
@RulesCollection(RulesCollection.Rules.BB2016)
public class StepPass extends AbstractStepWithReRoll {

	public static class StepState {
		public String goToLabelOnEnd;
		public String goToLabelOnMissedPass;
		public String CatcherId;
		public boolean passSkillUsed;
		public PassResult result;
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
		PassModifierFactory factory = game.getFactory(FactoryType.Factory.PASS_MODIFIER);
		PassMechanic mechanic = (PassMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
		PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(),
			false);
		Set<PassModifier> passModifiers = factory.findModifiers(new PassContext(game, game.getThrower(),
			passingDistance, false));
		Optional<Integer> minimumRollO = mechanic.minimumRoll(game.getThrower(), passingDistance, passModifiers);
		int minimumRoll = minimumRollO.orElse(0);
		int roll = minimumRollO.isPresent() ? getGameState().getDiceRoller().rollSkill() : 0;
		state.result = mechanic.evaluatePass(game.getThrower(), roll, passingDistance, passModifiers, PlayerAction.THROW_BOMB != game.getThrowerAction());
		if (PassResult.FUMBLE == state.result) {
			publishParameter(new StepParameter(StepParameterKey.DONT_DROP_FUMBLE, false));
		} else if (PassResult.SAVED_FUMBLE == state.result) {
			publishParameter(new StepParameter(StepParameterKey.DONT_DROP_FUMBLE, true));
		}
		boolean reRolled = ((getReRolledAction() == ReRolledActions.PASS) && (getReRollSource() != null));
		getResult().addReport(new ReportPassRoll(game.getThrowerId(), roll, minimumRoll, reRolled,
			passModifiers.toArray(new PassModifier[0]), passingDistance,
			(PlayerAction.THROW_BOMB == game.getThrowerAction()), state.result));
		if (PassResult.ACCURATE == state.result) {
			game.getFieldModel().setRangeRuler(null);
			publishParameter(new StepParameter(StepParameterKey.PASS_FUMBLE, false));
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
			if (mechanic.eligibleToReRoll(getReRolledAction(), game.getThrower())) {
				setReRolledAction(ReRolledActions.PASS);

				ReRollSource passingReroll = UtilCards.getRerollSource(game.getThrower(), ReRolledActions.PASS);
				if (passingReroll != null && !state.passSkillUsed) {
					doNextStep = false;
					state.passSkillUsed = true;
					Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
					UtilServerDialog.showDialog(getGameState(),
						new DialogSkillUseParameter(game.getThrowerId(), passingReroll.getSkill(game), minimumRoll),
						actingTeam.hasPlayer(game.getThrower()));
				} else {
					if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), game.getThrower(), ReRolledActions.PASS,
						minimumRoll, PassResult.FUMBLE == state.result)) {
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
		publishParameter(new StepParameter(StepParameterKey.PASS_FUMBLE, PassResult.FUMBLE == state.result));
		if (PassResult.SAVED_FUMBLE == state.result) {
			game.getFieldModel().setBallCoordinate(throwerCoordinate);
			game.getFieldModel().setBallMoving(false);
			getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnEnd);
		} else if (PassResult.FUMBLE == state.result) {
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
			publishParameter(new StepParameter(StepParameterKey.PASS_DEVIATES, PassResult.WILDLY_INACCURATE == state.result));
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
		IServerJsonOption.PASS_RESULT.addTo(jsonObject, state.result);
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
		state.result = (PassResult) IServerJsonOption.PASS_RESULT.getFrom(game, jsonObject);
		if (state.result == null) {
			boolean successful = IServerJsonOption.SUCCESSFUL.getFrom(game, jsonObject);
			boolean fumble = IServerJsonOption.PASS_FUMBLE.getFrom(game, jsonObject);
			boolean holdingSafeThrow = IServerJsonOption.HOLDING_SAFE_THROW.getFrom(game, jsonObject);
			if (successful) {
				state.result = PassResult.ACCURATE;
			} else if (fumble) {
				state.result = holdingSafeThrow ? PassResult.SAVED_FUMBLE : PassResult.FUMBLE;
			} else {
				state.result = PassResult.INACCURATE;
			}
		}
		state.passSkillUsed = IServerJsonOption.PASS_SKILL_USED.getFrom(game, jsonObject);
		return this;
	}

}
