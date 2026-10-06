package com.fumbbl.ffb.server.step.bb2025.pass;

import com.eclipsesource.json.JsonArray;
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
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.InterceptionContext;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.net.commands.ClientCommandInterceptorChoice;
import com.fumbbl.ffb.net.commands.ClientCommandReRollModifierChoice;
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
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;
import com.fumbbl.ffb.server.util.ReRollRequest;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.server.util.bb2025.InterceptionModifierSelectionService;
import com.fumbbl.ffb.server.util.bb2025.ReRollModifierChoiceDialogParameterFactory;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPassing;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
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
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepIntercept extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure;
	private Skill interceptionSkill;
	private int interceptionRoll;
	private boolean reRollUsed, awaitingRescue;
	private final Set<Skill> selectedModifierSkills = new LinkedHashSet<>();
	private final InterceptionModifierSelectionService selectionService = new InterceptionModifierSelectionService();

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
			switch (pReceivedCommand.getId()) {
				case CLIENT_INTERCEPTOR_CHOICE:
					ClientCommandInterceptorChoice interceptorCommand = (ClientCommandInterceptorChoice) pReceivedCommand
						.getCommand();
					PassState state = getGameState().getPassState();
					state.setInterceptorId(interceptorCommand.getInterceptorId());
					state.setInterceptorChosen(true);
					interceptionSkill = interceptorCommand.getInterceptionSkill();
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_RE_ROLL_MODIFIER_CHOICE:
					if (handleModifierChoice((ClientCommandReRollModifierChoice) pReceivedCommand.getCommand())) {
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
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

	private boolean handleModifierChoice(ClientCommandReRollModifierChoice command) {
		Game game = getGameState().getGame();
		PassState state = getGameState().getPassState();
		Player<?> interceptor = game.getPlayerById(state.getInterceptorId());
		if (!awaitingRescue || interceptor == null || easyIntercept(interceptor)
			|| command.getReRolledAction() != ReRolledActions.INTERCEPTION
			|| !interceptor.getId().equals(command.getPlayerId())) {
			return false;
		}
		Set<Skill> skills = new LinkedHashSet<>(command.getSkills());
		if (selectionService.findOptions(game, interceptor, state.getResult(), isBomb(state), interceptionRoll).stream()
			.noneMatch(option -> new LinkedHashSet<>(option.getSkills()).equals(skills))) {
			return false;
		}
		for (Skill skill : skills) {
			selectedModifierSkills.add(skill);
			interceptor.markUsed(skill, game);
			getResult().addReport(new ReportSkillUse(interceptor.getId(), skill, true, SkillUse.ADD_AGILITY_MODIFIER));
		}
		awaitingRescue = false;
		return true;
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
			if (awaitingRescue) {
				awaitingRescue = false;
				if (reRollUsed || (getReRollSource() == null)
					|| !UtilServerReRoll.useReRoll(this, getReRollSource(), interceptor)) {
					doIntercept = false;
				} else {
					reRollUsed = true;
					interceptionRoll = 0;
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
			state.setInterceptionSuccessful(doIntercept);
			if (doIntercept) {
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
			}
		}
	}

	private boolean easyIntercept(Player<?> interceptor) {
		return interceptionSkill != null && interceptor.hasUnused(interceptionSkill);
	}

	private boolean isBomb(PassState passState) {
		return StringTool.isProvided(passState.getOriginalBombardier());
	}

	private ActionStatus intercept(Player<?> pInterceptor, PassState passState) {
		boolean easyIntercept = easyIntercept(pInterceptor);

		ActionStatus status;
		Game game = getGameState().getGame();
		InterceptionModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.INTERCEPTION_MODIFIER);
		Set<InterceptionModifier> interceptionModifiers = modifierFactory.findModifiers(
			new InterceptionContext(game, pInterceptor, passState.getResult(), isBomb(passState),
				selectedModifierSkills));
		AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		int minimumRoll;
		InterceptionModifier[] interceptionModifierArray;

		if (easyIntercept) {
			minimumRoll = 2;
			interceptionModifierArray = new InterceptionModifier[0];
		} else {
			minimumRoll = mechanic.minimumRollInterception(pInterceptor, interceptionModifiers);
			interceptionModifierArray = interceptionModifiers.toArray(new InterceptionModifier[0]);
		}

		boolean doRoll = interceptionRoll == 0;
		if (doRoll) {
			interceptionRoll = getGameState().getDiceRoller().rollSkill();
		}

		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(interceptionRoll, minimumRoll);

		if (doRoll) {
			if (easyIntercept && !reRollUsed) {
				getResult().addReport(new ReportSkillUse(pInterceptor.getId(), interceptionSkill, true, SkillUse.EASY_INTERCEPT));
			}
			getResult().addReport(new ReportInterceptionRoll(pInterceptor.getId(), successful, interceptionRoll,
				minimumRoll, reRollUsed, interceptionModifierArray,
				PlayerAction.THROW_BOMB == game.getThrowerAction(), easyIntercept));
		}

		if (successful) {
			status = ActionStatus.SUCCESS;
			game.getFieldModel().setOutOfBounds(false);
			passState.setInterceptionSuccessful(true);
			if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
				game.getFieldModel().setBombMoving(false);
				publishParameter(StepParameter.from(StepParameterKey.INTERCEPTOR_ID, pInterceptor.getId()));
			} else {
				game.getFieldModel().setBallMoving(false);
			}
			if (easyIntercept) {
				getResult().setSound(SoundId.YOINK);
			}
		} else {
			status = offerRescue(pInterceptor, passState, minimumRoll, easyIntercept);
		}
		return status;
	}

	private ActionStatus offerRescue(Player<?> interceptor, PassState passState, int minimumRoll,
																	 boolean easyIntercept) {
		Game game = getGameState().getGame();
		setReRolledAction(ReRolledActions.INTERCEPTION);
		ReRollSource skillReRoll =
			reRollUsed ? null : UtilCards.getRerollSource(interceptor, ReRolledActions.INTERCEPTION);
		// an easy interception is a flat 2+ that ignores modifiers, so only a re-roll can rescue it
		List<ModifierChoiceOption> options = easyIntercept ? Collections.emptyList()
			: selectionService.findOptions(game, interceptor, passState.getResult(), isBomb(passState), interceptionRoll);

		if (options.isEmpty() && skillReRoll != null) {
			setReRollSource(skillReRoll);
			if (UtilServerReRoll.useReRoll(this, skillReRoll, interceptor)) {
				reRollUsed = true;
				interceptionRoll = 0;
				return intercept(interceptor, passState);
			}
			return ActionStatus.FAILURE;
		}

		List<ModifierChoiceOption> combinations = easyIntercept ? Collections.emptyList()
			: selectionService.findCombinations(game, interceptor, passState.getResult(), isBomb(passState));
		awaitingRescue = getGameState().getReRollService().askForReRollIfAvailable(
			ReRollRequest.forPlayer(getGameState(), interceptor, ReRolledActions.INTERCEPTION, minimumRoll)
				.reRollSkill(skillReRoll == null ? null : skillReRoll.getSkill(game))
				.dialogParameter(new ReRollModifierChoiceDialogParameterFactory(
					interceptionRoll, options, combinations, !reRollUsed))
				.build());

		return awaitingRescue ? ActionStatus.WAITING_FOR_RE_ROLL : ActionStatus.FAILURE;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		IServerJsonOption.SKILL.addTo(jsonObject, interceptionSkill);
		IServerJsonOption.INTERCEPTION_ROLL.addTo(jsonObject, interceptionRoll);
		IServerJsonOption.RE_ROLL_USED.addTo(jsonObject, reRollUsed);
		IServerJsonOption.AWAITING_RESCUE.addTo(jsonObject, awaitingRescue);
		JsonArray skills = new JsonArray();
		selectedModifierSkills.forEach(skill -> skills.add(skill.getName()));
		IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.addTo(jsonObject, skills);
		return jsonObject;
	}

	@Override
	public StepIntercept initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		interceptionSkill = (Skill) IServerJsonOption.SKILL.getFrom(source, jsonObject);
		interceptionRoll = IServerJsonOption.INTERCEPTION_ROLL.isDefinedIn(jsonObject)
			? IServerJsonOption.INTERCEPTION_ROLL.getFrom(source, jsonObject) : 0;
		reRollUsed = toPrimitive(IServerJsonOption.RE_ROLL_USED.getFrom(source, jsonObject));
		awaitingRescue = IServerJsonOption.AWAITING_RESCUE.isDefinedIn(jsonObject)
			? toPrimitive(IServerJsonOption.AWAITING_RESCUE.getFrom(source, jsonObject))
			: getReRolledAction() == ReRolledActions.INTERCEPTION;
		selectedModifierSkills.clear();
		JsonArray skills = IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.getFrom(source, jsonObject);
		if (skills != null) {
			SkillFactory factory = source.getFactory(FactoryType.Factory.SKILL);
			for (JsonValue skill : skills) {
				selectedModifierSkills.add(factory.forName(skill.asString()));
			}
		}
		return this;
	}

}
