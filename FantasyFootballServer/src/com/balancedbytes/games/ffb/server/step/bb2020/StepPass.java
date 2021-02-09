package com.balancedbytes.games.ffb.server.step.bb2020;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.factory.PassModifierFactory;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.mechanics.PassMechanic;
import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportPassRoll;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.bb2020.state.PassState;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;

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

	@Override
	public void init(StepParameterSet pParameterSet) {
		PassState state = getGameState().getPassState();
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					// mandatory
					case GOTO_LABEL_ON_END:
						state.setGoToLabelOnEnd((String) parameter.getValue());
						break;
					// mandatory
					case GOTO_LABEL_ON_MISSED_PASS:
						state.setGoToLabelOnMissedPass((String) parameter.getValue());
						break;
					case GOTO_LABEL_ON_SAVED_FUMBLE:
						state.setGoToLabelOnSavedFumble((String) parameter.getValue());
						break;
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(state.getGoToLabelOnEnd())) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
		if (!StringTool.isProvided(state.getGoToLabelOnMissedPass())) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_MISSED_PASS + " is not initialized.");
		}
		if (!StringTool.isProvided(state.getGoToLabelOnSavedFumble())) {
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
		state.setBombMode(PlayerAction.THROW_BOMB == game.getThrowerAction());
		if (state.isBombMode()) {
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
		state.setLandingCoordinate(game.getPassCoordinate());
		state.setThrowerId(game.getThrowerId());
		state.setThrowerCoordinate(throwerCoordinate);
		PassMechanic mechanic = (PassMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
		PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate, state.getLandingCoordinate(),
			false);
		Set<PassModifier> passModifiers = new PassModifierFactory().findPassModifiers(game, game.getThrower(),
			passingDistance, false);
		Optional<Integer> minimumRollO = mechanic.minimumRoll(game.getThrower(), passingDistance, passModifiers);
		int minimumRoll = minimumRollO.orElse(0);
		int roll = minimumRollO.isPresent() ? getGameState().getDiceRoller().rollSkill() : 0;
		state.setResult(mechanic.evaluatePass(game.getThrower(), roll, passingDistance, passModifiers, !state.isBombMode()));
		if (PassResult.FUMBLE == state.getResult()) {
			publishParameter(new StepParameter(StepParameterKey.DONT_DROP_FUMBLE, false));
		} else if (PassResult.SAVED_FUMBLE == state.getResult()) {
			publishParameter(new StepParameter(StepParameterKey.DONT_DROP_FUMBLE, true));
		}
		PassModifier[] passModifierArray = new PassModifierFactory().toArray(passModifiers);
		boolean reRolled = ((getReRolledAction() == ReRolledActions.PASS) && (getReRollSource() != null));
		getResult().addReport(new ReportPassRoll(game.getThrowerId(), roll, minimumRoll, reRolled,
			passModifierArray, passingDistance, (state.isBombMode()), state.getResult()));
		if (PassResult.ACCURATE == state.getResult()) {
			getResult().setNextAction(StepAction.GOTO_LABEL, state.getGoToLabelOnEnd());
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
			getResult().setNextAction(StepAction.GOTO_LABEL, state.getGoToLabelOnSavedFumble());
		} else if (PassResult.FUMBLE == state.getResult()) {
			if (state.isBombMode()) {
				game.getFieldModel().setBombCoordinate(throwerCoordinate);
			} else {
				game.getFieldModel().setBallCoordinate(throwerCoordinate);
				publishParameter(
					new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
			}
			publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, null));
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			if (state.isBombMode()) {
				game.getFieldModel().setBombCoordinate(state.getLandingCoordinate());
			} else {
				game.getFieldModel().setBallCoordinate(state.getLandingCoordinate());
			}
			publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, null));
			getResult().setNextAction(StepAction.GOTO_LABEL, state.getGoToLabelOnMissedPass());
		}
	}
}
