package com.balancedbytes.games.ffb.server.step.bb2020;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.modifiers.InterceptionContext;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.RangeRuler;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogInterceptionParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.InterceptionModifierFactory;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.mechanics.AgilityMechanic;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandInterceptorChoice;
import com.balancedbytes.games.ffb.report.ReportInterceptionRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
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
import com.balancedbytes.games.ffb.server.step.bb2020.state.PassState;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPassing;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Set;

/**
 * Step in the pass sequence to handle interceptions.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * <p>
 * Sets stepParameter INTERCEPTOR_ID for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepIntercept extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure;

	public StepIntercept(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INTERCEPT;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				// mandatory
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_FAILURE) {
					fGotoLabelOnFailure = (String) parameter.getValue();
				}
			}
		}
		if (fGotoLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_INTERCEPTOR_CHOICE) {
				ClientCommandInterceptorChoice interceptorCommand = (ClientCommandInterceptorChoice) pReceivedCommand
					.getCommand();
				PassState state = getGameState().getPassState();
				state.setInterceptorId(interceptorCommand.getInterceptorId());
				state.setInterceptorChosen(true);
				commandStatus = StepCommandStatus.EXECUTE_STEP;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		PassState state = getGameState().getPassState();
		if (game.getThrowerId() == null) {
			return;
		}
		// reset range ruler after passBlock
		if (game.getFieldModel().getRangeRuler() == null) {
			game.getFieldModel()
				.setRangeRuler(new RangeRuler(game.getThrowerId(), game.getPassCoordinate(), -1, false));
		}
		Player<?>[] possibleInterceptors = UtilPassing.findInterceptors(game, game.getPlayerById(game.getThrowerId()), game.getPassCoordinate());
		boolean doNextStep = true;
		boolean doIntercept = (possibleInterceptors.length > 0);
		if (doIntercept) {
			Player<?> interceptor = game.getPlayerById(state.getInterceptorId());
			if (ReRolledActions.INTERCEPTION == getReRolledAction()) {
				if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), interceptor)) {
					doIntercept = false;
				}
			}
			if (doIntercept) {
				if (!state.isInterceptorChosen()) {
					UtilServerDialog.showDialog(getGameState(), new DialogInterceptionParameter(game.getThrowerId()), true);
					state.setOldTurnMode(game.getTurnMode());
					game.setTurnMode(TurnMode.INTERCEPTION);
					doNextStep = false;
				} else if (interceptor != null) {
					switch (intercept(interceptor)) {
						case SUCCESS:
							doIntercept = true;
							break;
						case FAILURE:
							doIntercept = false;
							break;
						default:
							doNextStep = false;
							break;
					}
				} else {
					doIntercept = false;
				}
			}
		}
		if (doNextStep) {
			if (state.getOldTurnMode() != null) {
				game.setTurnMode(state.getOldTurnMode());
			}
			state.setInterceptionSuccessful(doIntercept);
			if (doIntercept) {
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
			}
		}
	}

	private ActionStatus intercept(Player<?> pInterceptor) {
		ActionStatus status;
		Game game = getGameState().getGame();
		InterceptionModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.INTERCEPTION_MODIFIER);
		Set<InterceptionModifier> interceptionModifiers = modifierFactory.findModifiers(new InterceptionContext(game, pInterceptor, getGameState().getPassState().getResult()));
		AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		int minimumRoll = mechanic.minimumRollInterception(pInterceptor, interceptionModifiers);
		int roll = getGameState().getDiceRoller().rollSkill();
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
		InterceptionModifier[] interceptionModifierArray = modifierFactory.sort(interceptionModifiers).toArray(new InterceptionModifier[0]);
		boolean reRolled = ((getReRolledAction() == ReRolledActions.CATCH) && (getReRollSource() != null));
		getResult().addReport(new ReportInterceptionRoll(pInterceptor.getId(), successful, roll, minimumRoll, reRolled,
			interceptionModifierArray, (PlayerAction.THROW_BOMB == game.getThrowerAction())));
		if (successful) {
			status = ActionStatus.SUCCESS;
		} else {
			status = ActionStatus.FAILURE;
			if (getReRolledAction() != ReRolledActions.CATCH) {
				setReRolledAction(ReRolledActions.CATCH);
				ReRollSource catchRerollSource = UtilCards.getRerollSource(game, pInterceptor, ReRolledActions.CATCH);
				if (catchRerollSource != null) {
					setReRollSource(catchRerollSource);
					UtilServerReRoll.useReRoll(this, getReRollSource(), pInterceptor);
					status = intercept(pInterceptor);
				} else {
					if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), pInterceptor, ReRolledActions.INTERCEPTION,
						minimumRoll, false)) {
						status = ActionStatus.WAITING_FOR_RE_ROLL;
					}
				}
			}
		}
		return status;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		return jsonObject;
	}

	@Override
	public StepIntercept initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		return this;
	}

}
