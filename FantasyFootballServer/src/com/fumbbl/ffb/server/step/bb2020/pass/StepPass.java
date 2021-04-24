package com.fumbbl.ffb.server.step.bb2020.pass;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.PassModifierFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportPassRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.bb2020.pass.state.PassState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Optional;
import java.util.Set;

/**
 * Step in the pass sequence to handle passing the ball.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_MISSED_PASS.
 * <p>
 * Expects stepParameter CATCHER_ID to be set by a preceding step.
 * <p>
 * Sets stepParameter CATCHER_ID for all steps on the stack. Sets stepParameter
 * PASS_ACCURATE for all steps on the stack. Sets stepParameter PASS_FUMBLE for
 * all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepPass extends AbstractStepWithReRoll {

	public StepPass(GameState pGameState) {
		super(pGameState);
		pGameState.setPassState(new PassState());
	}

	public StepId getId() {
		return StepId.PASS;
	}

	private String goToLabelOnEnd, goToLabelOnSavedFumble, goToLabelOnMissedPass;

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					// mandatory
					case GOTO_LABEL_ON_END:
						goToLabelOnEnd = (String) parameter.getValue();
						break;
					// mandatory
					case GOTO_LABEL_ON_MISSED_PASS:
						goToLabelOnMissedPass = (String) parameter.getValue();
						break;
					case GOTO_LABEL_ON_SAVED_FUMBLE:
						goToLabelOnSavedFumble = (String) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(goToLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
		if (!StringTool.isProvided(goToLabelOnMissedPass)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_MISSED_PASS + " is not initialized.");
		}
		if (!StringTool.isProvided(goToLabelOnSavedFumble)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_SAVED_FUMBLE + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			if (pParameter.getKey() == StepParameterKey.CATCHER_ID) {
				getGameState().getPassState().setCatcherId((String) pParameter.getValue());
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
			commandStatus = handleSkillCommand((ClientCommandUseSkill) pReceivedCommand.getCommand(), getGameState().getPassState());
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		PassState state = getGameState().getPassState();
		if ((game.getThrower() == null) || (game.getThrowerAction() == null)) {
			return;
		}
		if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
			game.getFieldModel().setBombMoving(true);
		} else {
			game.getFieldModel().setBallMoving(true);
		}
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
		if (ReRolledActions.PASS == getReRolledAction()) {
			if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), game.getThrower())) {
				handleFailedPass(throwerCoordinate);
				return;
			}
		}

		state.setThrowerCoordinate(throwerCoordinate);
		PassModifierFactory factory = game.getFactory(FactoryType.Factory.PASS_MODIFIER);
		PassMechanic mechanic = (PassMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
		PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(),
			false);
		Set<PassModifier> passModifiers = factory.findModifiers(new PassContext(game, game.getThrower(),
			passingDistance, false));
		Optional<Integer> minimumRollO = mechanic.minimumRoll(game.getThrower(), passingDistance, passModifiers);
		int minimumRoll = minimumRollO.orElse(0);
		int roll = minimumRollO.isPresent() ? getGameState().getDiceRoller().rollSkill() : 0;
		state.setResult(mechanic.evaluatePass(game.getThrower(), roll, passingDistance, passModifiers, PlayerAction.THROW_BOMB == game.getThrowerAction()));
		if (PassResult.FUMBLE == state.getResult()) {
			publishParameter(new StepParameter(StepParameterKey.DONT_DROP_FUMBLE, false));
		} else if (PassResult.SAVED_FUMBLE == state.getResult()) {
			publishParameter(new StepParameter(StepParameterKey.DONT_DROP_FUMBLE, true));
		}
		boolean reRolled = ((getReRolledAction() == ReRolledActions.PASS) && (getReRollSource() != null));
		getResult().addReport(new ReportPassRoll(game.getThrowerId(), roll, minimumRoll, reRolled,
			passModifiers.toArray(new PassModifier[0]), passingDistance, (PlayerAction.THROW_BOMB == game.getThrowerAction()), state.getResult()));
		if (PassResult.ACCURATE == state.getResult()) {
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnEnd);
		} else {
			boolean doNextStep = true;
			if (mechanic.eligibleToReRoll(getReRolledAction(), game.getThrower())) {
				setReRolledAction(ReRolledActions.PASS);

				ReRollSource passingReroll = UtilCards.getRerollSource(game.getThrower(), ReRolledActions.PASS);
				if (passingReroll != null && !state.isPassSkillUsed()) {
					doNextStep = false;
					state.setPassSkillUsed(true);
					Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
					UtilServerDialog.showDialog(getGameState(),
						new DialogSkillUseParameter(game.getThrowerId(), passingReroll.getSkill(game), minimumRoll),
						actingTeam.hasPlayer(game.getThrower()));
				} else {
					if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), game.getThrower(), ReRolledActions.PASS,
						minimumRoll, PassResult.FUMBLE == state.getResult())) {
						doNextStep = false;
					}
				}
			}
			if (doNextStep) {
				handleFailedPass(throwerCoordinate);
			}
		}
	}

	private void handleFailedPass(FieldCoordinate throwerCoordinate) {
		Game game = getGameState().getGame();
		PassState state = getGameState().getPassState();
		publishParameter(new StepParameter(StepParameterKey.PASS_FUMBLE, PassResult.FUMBLE == state.getResult()));
		if (PassResult.SAVED_FUMBLE == state.getResult()) {
			game.getFieldModel().setBallCoordinate(throwerCoordinate);
			game.getFieldModel().setBallMoving(false);
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnSavedFumble);
		} else if (PassResult.FUMBLE == state.getResult()) {
			if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
				game.getFieldModel().setBombCoordinate(throwerCoordinate);
			} else {
				game.getFieldModel().setBallCoordinate(throwerCoordinate);
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
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnMissedPass);
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, goToLabelOnEnd);
		IServerJsonOption.GOTO_LABEL_ON_MISSED_PASS.addTo(jsonObject, goToLabelOnMissedPass);
		IServerJsonOption.GOTO_LABEL_ON_SAVED_FUMBLE.addTo(jsonObject, goToLabelOnSavedFumble);
		return jsonObject;
	}

	@Override
	public StepPass initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		goToLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(game, jsonObject);
		goToLabelOnMissedPass = IServerJsonOption.GOTO_LABEL_ON_MISSED_PASS.getFrom(game, jsonObject);
		goToLabelOnSavedFumble = IServerJsonOption.GOTO_LABEL_ON_SAVED_FUMBLE.getFrom(game, jsonObject);
		return this;
	}
}
