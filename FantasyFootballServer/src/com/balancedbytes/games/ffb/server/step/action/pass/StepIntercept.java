package com.balancedbytes.games.ffb.server.step.action.pass;

import java.util.Set;

import com.balancedbytes.games.ffb.InterceptionModifier;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogInterceptionParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.InterceptionModifierFactory;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
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
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPassing;
import com.balancedbytes.games.ffb.util.UtilRangeRuler;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in the pass sequence to handle interceptions.
 *
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 *
 * Sets stepParameter INTERCEPTOR_ID for all steps on the stack.
 *
 * @author Kalimar
 */
public final class StepIntercept extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure;
	private String fInterceptorId;
	private boolean fInterceptorChosen;
	private TurnMode fOldTurnMode;

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
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_FAILURE:
					fGotoLabelOnFailure = (String) parameter.getValue();
					break;
				default:
					break;
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
			switch (pReceivedCommand.getId()) {
			case CLIENT_INTERCEPTOR_CHOICE:
				ClientCommandInterceptorChoice interceptorCommand = (ClientCommandInterceptorChoice) pReceivedCommand
						.getCommand();
				fInterceptorId = interceptorCommand.getInterceptorId();
				fInterceptorChosen = true;
				commandStatus = StepCommandStatus.EXECUTE_STEP;
				break;
			default:
				break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		if (game.getThrower() == null) {
			return;
		}
		// reset range ruler after passBlock
		if (game.getFieldModel().getRangeRuler() == null) {
			game.getFieldModel()
					.setRangeRuler(UtilRangeRuler.createRangeRuler(game, game.getThrower(), game.getPassCoordinate(), false));
		}
		Player<?>[] possibleInterceptors = UtilPassing.findInterceptors(game, game.getThrower(), game.getPassCoordinate());
		boolean doNextStep = true;
		boolean doIntercept = (possibleInterceptors.length > 0);
		if (doIntercept) {
			Player<?> interceptor = game.getPlayerById(fInterceptorId);
			if (ReRolledActions.INTERCEPTION == getReRolledAction()) {
				if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), interceptor)) {
					doIntercept = false;
				}
			}
			if (doIntercept) {
				if (!fInterceptorChosen) {
					UtilServerDialog.showDialog(getGameState(), new DialogInterceptionParameter(game.getThrowerId()), true);
					fOldTurnMode = game.getTurnMode();
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
			if (fOldTurnMode != null) {
				game.setTurnMode(fOldTurnMode);
			}
			if (doIntercept) {
				publishParameter(new StepParameter(StepParameterKey.INTERCEPTOR_ID, fInterceptorId));
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				publishParameter(new StepParameter(StepParameterKey.INTERCEPTOR_ID, null));
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
			}
		}
	}

	private ActionStatus intercept(Player<?> pInterceptor) {
		ActionStatus status = null;
		Game game = getGameState().getGame();
		InterceptionModifierFactory modifierFactory = new InterceptionModifierFactory();
		Set<InterceptionModifier> interceptionModifiers = modifierFactory.findInterceptionModifiers(game, pInterceptor);
		int minimumRoll = DiceInterpreter.getInstance().minimumRollInterception(pInterceptor, interceptionModifiers);
		int roll = getGameState().getDiceRoller().rollSkill();
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
		InterceptionModifier[] interceptionModifierArray = modifierFactory.toArray(interceptionModifiers);
		boolean reRolled = ((getReRolledAction() == ReRolledActions.CATCH) && (getReRollSource() != null));
		getResult().addReport(new ReportInterceptionRoll(pInterceptor.getId(), successful, roll, minimumRoll, reRolled,
				interceptionModifierArray, (PlayerAction.THROW_BOMB == game.getThrowerAction())));
		if (successful) {
			status = ActionStatus.SUCCESS;
		} else {
			status = ActionStatus.FAILURE;
			if (getReRolledAction() != ReRolledActions.CATCH) {
				setReRolledAction(ReRolledActions.CATCH);
				ReRollSource catchRerollSource = UtilCards.getRerollSource(pInterceptor, ReRolledActions.CATCH);
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
		IServerJsonOption.INTERCEPTOR_ID.addTo(jsonObject, fInterceptorId);
		IServerJsonOption.INTERCEPTOR_CHOSEN.addTo(jsonObject, fInterceptorChosen);
		IServerJsonOption.OLD_TURN_MODE.addTo(jsonObject, fOldTurnMode);
		return jsonObject;
	}

	@Override
	public StepIntercept initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		fInterceptorId = IServerJsonOption.INTERCEPTOR_ID.getFrom(game, jsonObject);
		fInterceptorChosen = IServerJsonOption.INTERCEPTOR_CHOSEN.getFrom(game, jsonObject);
		fOldTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(game, jsonObject);
		return this;
	}

}
