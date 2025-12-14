package com.fumbbl.ffb.server.step.bb2020.pass;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogInterceptionParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.InterceptionModifierFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.InterceptionContext;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandInterceptorChoice;
import com.fumbbl.ffb.report.ReportInterceptionRoll;
import com.fumbbl.ffb.report.ReportSkillUse;
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
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPassing;

import java.util.Arrays;
import java.util.Optional;
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
	private Skill interceptionSkill;

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
				interceptionSkill = interceptorCommand.getInterceptionSkill();
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
		Player<?> interceptor = game.getPlayerById(state.getInterceptorId());
		if (doIntercept) {
			if (ReRolledActions.INTERCEPTION == getReRolledAction()) {
				if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), interceptor)) {
					doIntercept = false;
				}
			}
			if (doIntercept) {
				if (!state.isInterceptorChosen()) {

					Optional<Skill> foundSkill = Arrays.stream(possibleInterceptors)
						.map(player -> UtilCards.getUnusedSkillWithProperty(player, NamedProperties.canInterceptEasily))
						.filter(Optional::isPresent).map(Optional::get).findFirst();

					if (foundSkill.isPresent()) {
						UtilServerDialog.showDialog(getGameState(), new DialogInterceptionParameter(game.getThrowerId(), foundSkill.get(), 'o'), true);
					} else {
						UtilServerDialog.showDialog(getGameState(), new DialogInterceptionParameter(game.getThrowerId()), true);
					}
					state.setOldTurnMode(game.getTurnMode());
					game.setTurnMode(TurnMode.INTERCEPTION);
					doNextStep = false;
				} else if (interceptor != null) {
					switch (intercept(interceptor, state)) {
						case SUCCESS:
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
			if (interceptionSkill != null && interceptor != null) {
				interceptor.markUsed(interceptionSkill, game);
			}
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

	private ActionStatus intercept(Player<?> pInterceptor, PassState passState) {
		boolean easyIntercept = interceptionSkill != null && pInterceptor.hasUnused(interceptionSkill);

		ActionStatus status;
		Game game = getGameState().getGame();
		InterceptionModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.INTERCEPTION_MODIFIER);
		Set<InterceptionModifier> interceptionModifiers = modifierFactory.findModifiers(
			new InterceptionContext(game, pInterceptor, getGameState().getPassState().getResult(),
				StringTool.isProvided(passState.getOriginalBombardier())));
		AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		int minimumRoll;
		InterceptionModifier[] interceptionModifierArray;
		int roll = getGameState().getDiceRoller().rollSkill();

		if (easyIntercept) {
			minimumRoll = 2;
			interceptionModifierArray = new InterceptionModifier[0];
		} else {
			minimumRoll = mechanic.minimumRollInterception(pInterceptor, interceptionModifiers);
			interceptionModifierArray = interceptionModifiers.toArray(new InterceptionModifier[0]);
		}

		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
		boolean reRolled = ((getReRolledAction() == ReRolledActions.INTERCEPTION) && (getReRollSource() != null));

		if (easyIntercept && !reRolled) {
			getResult().addReport(new ReportSkillUse(pInterceptor.getId(), interceptionSkill, true, SkillUse.EASY_INTERCEPT));
		}

		boolean isBomb = PlayerAction.THROW_BOMB == game.getThrowerAction();
		getResult().addReport(new ReportInterceptionRoll(pInterceptor.getId(), successful, roll, minimumRoll, reRolled,
			interceptionModifierArray, isBomb, easyIntercept));
		if (successful) {
			status = ActionStatus.SUCCESS;
			game.getFieldModel().setOutOfBounds(false);
			if (easyIntercept) {
				if (isBomb) {
					game.getFieldModel().setBombMoving(false);
					publishParameter(StepParameter.from(StepParameterKey.INTERCEPTOR_ID, pInterceptor.getId()));
				} else {
					game.getFieldModel().setBallMoving(false);
				}
				passState.setInterceptionSuccessful(true);
				getResult().setSound(SoundId.YOINK);
			}
		} else {
			status = ActionStatus.FAILURE;
			if (getReRolledAction() != ReRolledActions.INTERCEPTION) {
				setReRolledAction(ReRolledActions.INTERCEPTION);
				ReRollSource skillRerollSource = UtilCards.getRerollSource(pInterceptor, ReRolledActions.INTERCEPTION);
				if (skillRerollSource != null) {
					setReRollSource(skillRerollSource);
					UtilServerReRoll.useReRoll(this, getReRollSource(), pInterceptor);
					status = intercept(pInterceptor, passState);
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
		IServerJsonOption.SKILL.addTo(jsonObject, interceptionSkill);
		return jsonObject;
	}

	@Override
	public StepIntercept initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		interceptionSkill = (Skill) IServerJsonOption.SKILL.getFrom(source, jsonObject);
		return this;
	}

}
