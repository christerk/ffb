package com.fumbbl.ffb.server.step.bb2020.pass;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogInterceptionParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.InterceptionModifierFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.InterceptionContext;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandInterceptorChoice;
import com.fumbbl.ffb.report.ReportInterceptionRoll;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
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
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPassing;

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
		if (game.getThrowerId() == null || PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction()
			|| PlayerAction.HAIL_MARY_PASS == game.getThrowerAction()) {
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
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
			state.setDeflectionSuccessful(doIntercept);
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
		InterceptionModifier[] interceptionModifierArray = interceptionModifiers.toArray(new InterceptionModifier[0]);
		boolean reRolled = ((getReRolledAction() == ReRolledActions.INTERCEPTION) && (getReRollSource() != null));
		getResult().addReport(new ReportInterceptionRoll(pInterceptor.getId(), successful, roll, minimumRoll, reRolled,
			interceptionModifierArray, (PlayerAction.THROW_BOMB == game.getThrowerAction())));
		if (successful) {
			status = ActionStatus.SUCCESS;
		} else {
			status = ActionStatus.FAILURE;
			if (getReRolledAction() != ReRolledActions.INTERCEPTION) {
				setReRolledAction(ReRolledActions.INTERCEPTION);
				ReRollSource skillRerollSource = UtilCards.getRerollSource(pInterceptor, ReRolledActions.INTERCEPTION);
				if (skillRerollSource != null) {
					setReRollSource(skillRerollSource);
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
