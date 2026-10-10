package com.fumbbl.ffb.server.step.bb2025.move;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.JumpModifierFactory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandReRollModifierChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportJumpRoll;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeDropJump;
import com.fumbbl.ffb.server.model.SteadyFootingContext;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.bb2025.shared.StallingExtension;
import com.fumbbl.ffb.server.util.ReRollRequest;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.server.util.bb2025.JumpModifierSelectionService;
import com.fumbbl.ffb.server.util.bb2025.ReRollModifierChoiceDialogParameterFactory;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Step in move sequence to handle jumps.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * <p>
 * Sets stepParameter INJURY_TYPE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepJump extends AbstractStepWithReRoll {

	private static final String STALLING_EXPLANATION =
		"You are only asked because failing the jump would end the turn and skip the stalling roll.";

	private final JumpModifierSelectionService selectionService = new JumpModifierSelectionService();
	private final StallingExtension stallingExtension = new StallingExtension();

	private String goToLabelOnFailure;
	private FieldCoordinate moveStart;
	private int roll;
	private Boolean usingDivingTackle;
	private boolean alreadyReported;
	private ActionStatus status;
	private Boolean useIgnoreModifierAfterRollSkill;
	private boolean useIgnoreModifierSkill;
	private boolean dtRerollAsked;
	private final Set<Skill> selectedModifierSkills = new LinkedHashSet<>();
	private final Set<Skill> declinedFreeSkills = new LinkedHashSet<>();
	private Skill pendingFreeSkill;
	private boolean modifierChoiceOffered;
	private Boolean freeModifiersOptional;
	private boolean modifierChoiceApplied;

	public StepJump(GameState pGameState) {
		super(pGameState);

	}

	public StepId getId() {
		return StepId.JUMP;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					// mandatory
					case GOTO_LABEL_ON_FAILURE:
						goToLabelOnFailure = (String) parameter.getValue();
						break;
					case MOVE_START:
						moveStart = (FieldCoordinate) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		if (goToLabelOnFailure == null) {
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
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_PLAYER_CHOICE) {
				ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
				if (playerChoiceCommand.getPlayerChoiceMode() == PlayerChoiceMode.DIVING_TACKLE) {
					usingDivingTackle = StringTool.isProvided(playerChoiceCommand.getPlayerId());
					getGameState().getGame().setDefenderId(playerChoiceCommand.getPlayerId());
					commandStatus = StepCommandStatus.EXECUTE_STEP;
				}
			}
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
				ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
				if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canChooseToIgnoreJumpModifierAfterRoll)) {
					useIgnoreModifierAfterRollSkill = commandUseSkill.isSkillUsed();
					commandStatus = StepCommandStatus.EXECUTE_STEP;
				} else if (pendingFreeSkill != null && pendingFreeSkill.equals(commandUseSkill.getSkill())) {
					commitFreeModifierSkill(commandUseSkill.isSkillUsed());
					commandStatus = StepCommandStatus.EXECUTE_STEP;
				}
			}
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_RE_ROLL_MODIFIER_CHOICE
				&& commitModifierChoice((ClientCommandReRollModifierChoice) pReceivedCommand.getCommand())) {
				commandStatus = StepCommandStatus.EXECUTE_STEP;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	/**
	 * Applies the optional modifiers the coach picked instead of a re-roll. They are marked used right away, as all
	 * modifiers of the jumping player have to be declared before Diving Tackle is decided.
	 *
	 * @return whether the choice was accepted
	 */
	private boolean commitModifierChoice(ClientCommandReRollModifierChoice command) {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (command.getReRolledAction() != ReRolledActions.JUMP || actingPlayer.getPlayer() == null
			|| command.getSkills().isEmpty()) {
			return false;
		}
		for (Skill skill : command.getSkills()) {
			if (skill != null && selectedModifierSkills.add(skill)) {
				actingPlayer.markSkillUsed(skill);
				getResult()
					.addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.ADD_AGILITY_MODIFIER));
			}
		}
		modifierChoiceApplied = true;
		modifierChoiceOffered = true;
		return true;
	}

	/**
	 * Applies the answer to the skill use dialog for a free and unlimited modifier skill. Those skills are never
	 * spent, so they must not be marked used, they are only remembered for the current jump.
	 */
	private void commitFreeModifierSkill(boolean used) {
		ActingPlayer actingPlayer = getGameState().getGame().getActingPlayer();
		Skill skill = pendingFreeSkill;
		pendingFreeSkill = null;
		if (used) {
			selectedModifierSkills.add(skill);
		} else {
			declinedFreeSkills.add(skill);
		}
		// either way the already rolled die has to be evaluated again instead of falling through to the failure
		modifierChoiceApplied = true;
		getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, used, SkillUse.ADD_AGILITY_MODIFIER));
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case MOVE_START: {
					moveStart = (FieldCoordinate) parameter.getValue();
					return true;
				}
			}
		}
		return false;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		JumpMechanic mechanic =
			(JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
		boolean doLeap = (actingPlayer.isJumping() && mechanic.canStillJump(game, actingPlayer));
		if (doLeap) {
			if (ReRolledActions.JUMP == getReRolledAction()
				&& !Boolean.TRUE.equals(useIgnoreModifierAfterRollSkill)
				&& usingDivingTackle == null && !modifierChoiceApplied) {
				if (!dtRerollAsked && (getReRollSource() == null || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer()))) {
					if (!Boolean.TRUE.equals(useIgnoreModifierAfterRollSkill)) {
						handleFailure(game);
						doLeap = false;
					}
				}
			}
			useIgnoreModifierSkill = actingPlayer.isJumpsWithoutModifiers();
			if (doLeap) {
				if (useIgnoreModifierSkill) {
					Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canIgnoreJumpModifiers);
					if (skill == null) {
						useIgnoreModifierSkill = false;
					} else {
						usingDivingTackle = false;
						getResult().addReport(
							new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.PASS_JUMP_WITHOUT_MODIFIERS));
					}
				}
				switch (leap()) {
					case SUCCESS:
						actingPlayer.setJumping(false);
						getResult().setNextAction(StepAction.NEXT_STEP);
						break;
					case FAILURE:
						handleFailure(game);
						break;
					case WAITING_FOR_RE_ROLL:
						getResult().setNextAction(StepAction.CONTINUE);
						break;
					default:
						break;
				}
			}
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private void handleFailure(Game game) {
		ActingPlayer actingPlayer = game.getActingPlayer();
		actingPlayer.setJumping(false);
		publishParameter(new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT,
			new SteadyFootingContext(new InjuryTypeDropJump(game.getDefender()))));
		if (roll > 1) {
			publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, moveStart));
		} else {
			publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, null));
			game.getFieldModel().updatePlayerAndBallPosition(actingPlayer.getPlayer(), moveStart);
			UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isJumping());
		}
		getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnFailure);
	}

	private ActionStatus leap() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		boolean reRolled = ((getReRolledAction() == ReRolledActions.JUMP) && (getReRollSource() != null));

		if (!reRolled) {
			publishParameter(StepParameter.from(StepParameterKey.JUMPED, true));
		}

		FieldCoordinate to = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		JumpModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.JUMP_MODIFIER);
		JumpContext context = new JumpContext(game, actingPlayer.getPlayer(), moveStart, to, selectedModifierSkills,
			freeModifiersOptional(game));
		Set<JumpModifier> divingTackleModifiers = new HashSet<>();
		if (usingDivingTackle != null && usingDivingTackle) {
			Optional<Skill> skill = game.getDefender().getSkillsIncludingTemporaryOnes().stream()
				.filter(s -> s.getSkillProperties().contains(NamedProperties.canAttemptToTackleJumpingPlayer)).findFirst();
			if (skill.isPresent()) {

				if (!alreadyReported) {
					publishParameter(new StepParameter(StepParameterKey.USING_DIVING_TACKLE, true));
					alreadyReported = true;
					getResult().addReport(
						new ReportSkillUse(game.getDefender().getId(), skill.get(), true, SkillUse.STOP_OPPONENT));
				}

				skill.get().getJumpModifiers().forEach(modifier -> {
					context.addModifierValue(modifier.getModifier());
					divingTackleModifiers.add(modifier);
				});
			}
		}
		Set<JumpModifier> jumpModifiers = new HashSet<>();
		if (!useIgnoreModifierSkill) {
			jumpModifiers.addAll(modifierFactory.findModifiers(context));
			jumpModifiers.addAll(divingTackleModifiers);
		}
		AgilityMechanic mechanic =
			(AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		int minimumRoll = mechanic.minimumRollJump(actingPlayer.getPlayer(), jumpModifiers);

		boolean doRoll = !modifierChoiceApplied && (usingDivingTackle == null || useIgnoreModifierSkill)
			&& (reRolled || ((status == null || status == ActionStatus.WAITING_FOR_RE_ROLL) && !dtRerollAsked));
		// the choice has been evaluated now, when it did not rescue the jump a following re-roll has to roll again
		modifierChoiceApplied = false;
		if (doRoll) {
			roll = getGameState().getDiceRoller().rollSkill();
			// a fresh die may be improved by the very same skills again
			modifierChoiceOffered = false;
		}

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		boolean successfulWithoutModifiers = diceInterpreter.isSkillRollSuccessful(roll,
			mechanic.minimumRollJump(actingPlayer.getPlayer(), Collections.emptySet()));
		Skill ignoreModifiersAfterRollSkill = null;
		if (successfulWithoutModifiers) {
			ignoreModifiersAfterRollSkill =
				UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canChooseToIgnoreJumpModifierAfterRoll);
		}

		if (Boolean.TRUE.equals(useIgnoreModifierAfterRollSkill) && ignoreModifiersAfterRollSkill != null) {
			actingPlayer.markSkillUsed(ignoreModifiersAfterRollSkill);
			getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), ignoreModifiersAfterRollSkill, true,
				SkillUse.PASS_JUMP_WITHOUT_MODIFIERS));
			status = ActionStatus.SUCCESS;
			return status;
		}

		boolean successful =
			useIgnoreModifierSkill ? successfulWithoutModifiers : diceInterpreter.isSkillRollSuccessful(roll, minimumRoll);

		if (doRoll) {
			getResult().addReport(new ReportJumpRoll(actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled,
				jumpModifiers.toArray(new JumpModifier[0])));
		}

		if (successful) {
			if (usingDivingTackle == null) {
				status = checkDivingTackle(game, new JumpContext(game, actingPlayer.getPlayer(), moveStart, to,
					selectedModifierSkills, freeModifiersOptional(game)), modifierFactory, mechanic,
					ignoreModifiersAfterRollSkill);
			} else {
				status = ActionStatus.SUCCESS;
			}
		} else {
			status = ActionStatus.FAILURE;
			Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canIgnoreJumpModifiers);
			Set<Skill> ignoreSkills = new HashSet<>();
			if (skill != null) {
				ignoreSkills.add(skill);
			}

			// declining a free modifier is a deliberate choice to fail the jump, so nothing is offered to rescue it
			boolean rescueDeclined = !declinedFreeSkills.isEmpty();

			// the dialog offering the modifiers cannot show the skill ignoring the modifiers after the roll,
			// so that skill keeps its own dialog
			List<ModifierChoiceOption> options = ignoreModifiersAfterRollSkill == null && !rescueDeclined
				? findOptions(game, actingPlayer, to, divingTackleModifiers)
				: Collections.emptyList();
			List<ModifierChoiceOption> combinations =
				options.isEmpty() ? Collections.emptyList() : findCombinations(game, actingPlayer, to,
					divingTackleModifiers);

			if (ignoreModifiersAfterRollSkill == null
				&& askForFreeModifierSkill(game, actingPlayer, to, divingTackleModifiers)) {
				status = ActionStatus.WAITING_FOR_SKILL_USE;
			} else if (rescueDeclined) {
				// the free modifier was declined on purpose, neither modifiers nor re-rolls are offered any more
				status = ActionStatus.FAILURE;
			} else if (getReRolledAction() != ReRolledActions.JUMP) {
				setReRolledAction(ReRolledActions.JUMP);

				ReRollSource skillReRollSource = UtilCards.getUnusedRerollSource(actingPlayer, ReRolledActions.JUMP);

				boolean automaticReRoll = skillReRollSource != null &&
					(skill == null || skill.getRerollSource(ReRolledActions.JUMP) != skillReRollSource ||
						useIgnoreModifierSkill);

				// the coach has to decide themselves when there are modifiers to pick from or when failing the jump
				// would skip the stalling roll for a team mate
				if (automaticReRoll && options.isEmpty()
					&& !stallingExtension.wouldEndOfTurnTriggerStallingRoll(game, actingPlayer.getPlayer())) {
					status = ActionStatus.WAITING_FOR_RE_ROLL;
					status = leap();
				} else if (askForRescue(minimumRoll, options, combinations, true, ignoreModifiersAfterRollSkill,
					ignoreSkills)) {
					modifierChoiceOffered = !options.isEmpty();
					status = ActionStatus.WAITING_FOR_RE_ROLL;
				}
			} else if (useIgnoreModifierAfterRollSkill == null && ignoreModifiersAfterRollSkill != null) {
				UtilServerDialog.showDialog(getGameState(),
					new DialogSkillUseParameter(actingPlayer.getPlayerId(), ignoreModifiersAfterRollSkill, 0), false);
				return ActionStatus.WAITING_FOR_SKILL_USE;
			} else if (!options.isEmpty()
				&& askForRescue(minimumRoll, options, combinations, false, null, ignoreSkills)) {
				// the re-roll is gone, but optional modifiers can still save the jump
				modifierChoiceOffered = true;
				status = ActionStatus.WAITING_FOR_RE_ROLL;
			}
		}
		if (useIgnoreModifierSkill) {
			actingPlayer.markSkillUsed(NamedProperties.canIgnoreJumpModifiers);
		}
		return status;
	}

	private List<ModifierChoiceOption> findOptions(Game game, ActingPlayer actingPlayer, FieldCoordinate to,
		Set<JumpModifier> extraModifiers) {
		if (modifierChoiceOffered || useIgnoreModifierSkill) {
			return Collections.emptyList();
		}
		return selectionService.findOptions(game, actingPlayer, moveStart, to, selectedModifierSkills, extraModifiers,
			freeModifiersOptional(game), roll);
	}

	private List<ModifierChoiceOption> findCombinations(Game game, ActingPlayer actingPlayer, FieldCoordinate to,
		Set<JumpModifier> extraModifiers) {
		return selectionService.findCombinations(game, actingPlayer, moveStart, to, selectedModifierSkills,
			extraModifiers, freeModifiersOptional(game));
	}

	/**
	 * Offers the given modifier options together with the available re-rolls in a single dialog.
	 *
	 * @return whether the dialog was shown
	 */
	private boolean askForRescue(int minimumRoll, List<ModifierChoiceOption> options,
		List<ModifierChoiceOption> combinations, boolean reRollPossible, Skill modifyingSkill, Set<Skill> ignoreSkills) {
		return askForRescue(minimumRoll, options, combinations, reRollPossible, modifyingSkill, ignoreSkills, null);
	}

	private boolean askForRescue(int minimumRoll, List<ModifierChoiceOption> options,
		List<ModifierChoiceOption> combinations, boolean reRollPossible, Skill modifyingSkill, Set<Skill> ignoreSkills,
		List<String> messages) {
		GameState gameState = getGameState();
		ReRollRequest.Builder builder =
			ReRollRequest.forActingPlayer(gameState, gameState.getGame().getActingPlayer(), ReRolledActions.JUMP,
					minimumRoll)
				.modifyingSkill(modifyingSkill)
				.ignoreSkills(ignoreSkills)
				.messages(messages);
		if (!options.isEmpty() || !reRollPossible) {
			builder.dialogParameter(
				new ReRollModifierChoiceDialogParameterFactory(roll, options, combinations, reRollPossible));
		}
		return gameState.getReRollService().askForReRollIfAvailable(builder.build());
	}

	/**
	 * Leap, Very Long Legs and Pogo can be used on every jump and cost nothing, the coach is only asked about them
	 * because failing the jump would skip a stalling roll for a team mate. That is a plain skill use question, so it
	 * gets its own dialog instead of being mixed into the modifier choice.
	 *
	 * @return whether the dialog was shown
	 */
	private boolean askForFreeModifierSkill(Game game, ActingPlayer actingPlayer, FieldCoordinate to,
		Set<JumpModifier> extraModifiers) {
		if (pendingFreeSkill != null || useIgnoreModifierSkill || !freeModifiersOptional(game)) {
			return false;
		}
		Optional<Skill> candidate = selectionService.findFreeSkills(game, actingPlayer, moveStart, to,
				selectedModifierSkills, extraModifiers, true, roll).stream()
			.filter(skill -> !declinedFreeSkills.contains(skill)).findFirst();
		if (!candidate.isPresent()) {
			return false;
		}
		pendingFreeSkill = candidate.get();
		Set<Skill> withSkill = new LinkedHashSet<>(selectedModifierSkills);
		withSkill.add(pendingFreeSkill);
		int minimumRoll = selectionService.minimumRoll(game, actingPlayer, moveStart, to, withSkill, extraModifiers, true);
		UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(actingPlayer.getPlayerId(),
			pendingFreeSkill, minimumRoll, Collections.singletonList(STALLING_EXPLANATION)), false);
		return true;
	}

	/**
	 * Free modifiers may only be declined when failing the jump would skip the stalling roll for a team mate, otherwise
	 * declining them would never be a meaningful choice.
	 */
	private boolean freeModifiersOptional(Game game) {
		if (freeModifiersOptional == null) {
			freeModifiersOptional = stallingExtension.wouldEndOfTurnTriggerStallingRoll(game,
				game.getActingPlayer().getPlayer());
		}
		return freeModifiersOptional;
	}

	private ActionStatus checkDivingTackle(Game game, JumpContext context, JumpModifierFactory modifierFactory,
		AgilityMechanic mechanic, Skill ignoreModifiersAfterRollSkill) {
		boolean ignoresDT = (UtilCards.hasSkillToCancelProperty(game.getActingPlayer().getPlayer(),
			NamedProperties.canAttemptToTackleJumpingPlayer));

		if (ignoresDT) {
			return ActionStatus.SUCCESS;
		}

		Player<?>[] divingTacklers = UtilPlayer.findEligibleDivingTacklers(game, context.getFrom(),
			context.getTo(), NamedProperties.canAttemptToTackleJumpingPlayer);

		if (!ArrayTool.isProvided(divingTacklers)) {
			return ActionStatus.SUCCESS;
		}

		Optional<Skill> skill = divingTacklers[0].getSkillsIncludingTemporaryOnes().stream()
			.filter(s -> s.getSkillProperties().contains(NamedProperties.canAttemptToTackleJumpingPlayer)).findFirst();
		if (!skill.isPresent()) {
			return ActionStatus.SUCCESS;
		}

		skill.get().getJumpModifiers().forEach(modifier -> context.addModifierValue(modifier.getModifier()));
		Set<JumpModifier> jumpModifiers = modifierFactory.findModifiers(context);
		jumpModifiers.addAll(skill.get().getJumpModifiers());
		int minimumRoll = mechanic.minimumRollJump(context.getPlayer(), jumpModifiers);
		boolean tripsJumper = !DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);

		// all modifiers of the jumping player have to be declared before Diving Tackle is decided, so the
		// coach gets the same options as on a real failure
		if (tripsJumper && !dtRerollAsked && !modifierChoiceOffered) {
			ActingPlayer actingPlayer = game.getActingPlayer();
			Set<JumpModifier> divingTackleModifiers = new HashSet<>(skill.get().getJumpModifiers());
			if (ignoreModifiersAfterRollSkill == null
				&& askForFreeModifierSkill(game, actingPlayer, context.getTo(), divingTackleModifiers)) {
				return ActionStatus.WAITING_FOR_SKILL_USE;
			}
			// declining a free modifier is a deliberate choice to fail the jump, so nothing is offered to
			// rescue it and Diving Tackle is simply applied
			if (declinedFreeSkills.isEmpty()) {
				// the dialog offering the modifiers cannot show the skill ignoring the modifiers after the roll,
				// so that skill keeps its own dialog
				List<ModifierChoiceOption> options = ignoreModifiersAfterRollSkill == null
					? findOptions(game, actingPlayer, context.getTo(), divingTackleModifiers)
					: Collections.emptyList();
				List<ModifierChoiceOption> combinations = options.isEmpty() ? Collections.emptyList()
					: findCombinations(game, actingPlayer, context.getTo(), divingTackleModifiers);
				boolean reRollPossible = getReRolledAction() != ReRolledActions.JUMP;
				List<String> messages = Collections.singletonList(options.isEmpty()
					? "Diving Tackle can make this jump fail. Reroll the jump now?"
					: "Diving Tackle can make this jump fail.");
				if ((!options.isEmpty() || reRollPossible) && askForRescue(minimumRoll, options, combinations,
					reRollPossible, ignoreModifiersAfterRollSkill, Collections.emptySet(), messages)) {
					dtRerollAsked = true;
					modifierChoiceOffered = true;
					return ActionStatus.WAITING_FOR_RE_ROLL;
				}
				if (ignoreModifiersAfterRollSkill != null && useIgnoreModifierAfterRollSkill == null) {
					dtRerollAsked = true;
					UtilServerDialog.showDialog(getGameState(),
						new DialogSkillUseParameter(actingPlayer.getPlayerId(), ignoreModifiersAfterRollSkill, 0), false);
					return ActionStatus.WAITING_FOR_SKILL_USE;
				}
			}
		}

		// the opposing coach decides even when the jump cannot be stopped any more, the dialog explains why
		String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
		UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(teamId,
			PlayerChoiceMode.DIVING_TACKLE, divingTacklers, new String[]{divingTackleDescription(tripsJumper)}, 1), true);

		return ActionStatus.WAITING_FOR_SKILL_USE;
	}

	private String divingTackleDescription(boolean tripsJumper) {
		if (tripsJumper) {
			return "This will trip the jumper.";
		}
		if (selectedModifierSkills.isEmpty()) {
			return "This will NOT trip the jumper, the jump will still succeed.";
		}
		return "This will NOT trip the jumper, but will force the use of "
			+ selectedModifierSkills.stream().map(Skill::getName).collect(Collectors.joining(" + ")) + ".";
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToLabelOnFailure);
		IServerJsonOption.MOVE_START.addTo(jsonObject, moveStart);
		IServerJsonOption.ROLL.addTo(jsonObject, roll);
		IServerJsonOption.USING_DIVING_TACKLE.addTo(jsonObject, usingDivingTackle);
		IServerJsonOption.ALREADY_REPORTED.addTo(jsonObject, alreadyReported);
		IServerJsonOption.USING_MODIFIER_IGNORING_SKILL_BEFORE_ROLL.addTo(jsonObject, useIgnoreModifierAfterRollSkill);
		IServerJsonOption.USING_MODIFIER_IGNORING_SKILL.addTo(jsonObject, useIgnoreModifierAfterRollSkill);
		if (status != null) {
			IServerJsonOption.STATUS.addTo(jsonObject, status.name());
		}
		IServerJsonOption.DT_REROLL_ASKED.addTo(jsonObject, dtRerollAsked);
		IServerJsonOption.MODIFIER_CHOICE_OFFERED.addTo(jsonObject, modifierChoiceOffered);
		IServerJsonOption.FREE_MODIFIERS_OPTIONAL.addTo(jsonObject, freeModifiersOptional);
		JsonArray skills = new JsonArray();
		selectedModifierSkills.forEach(skill -> skills.add(skill.getName()));
		IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.addTo(jsonObject, skills);
		JsonArray declinedSkills = new JsonArray();
		declinedFreeSkills.forEach(skill -> declinedSkills.add(skill.getName()));
		IServerJsonOption.DECLINED_AGILITY_MODIFIER_SKILLS.addTo(jsonObject, declinedSkills);
		IServerJsonOption.PENDING_AGILITY_MODIFIER_SKILL.addTo(jsonObject,
			pendingFreeSkill != null ? pendingFreeSkill.getName() : null);
		return jsonObject;
	}

	@Override
	public StepJump initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		moveStart = IServerJsonOption.MOVE_START.getFrom(source, jsonObject);
		roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		usingDivingTackle = IServerJsonOption.USING_DIVING_TACKLE.getFrom(source, jsonObject);
		alreadyReported = IServerJsonOption.ALREADY_REPORTED.getFrom(source, jsonObject);
		useIgnoreModifierAfterRollSkill = IServerJsonOption.USING_MODIFIER_IGNORING_SKILL.getFrom(source, jsonObject);
		useIgnoreModifierSkill =
			toPrimitive(IServerJsonOption.USING_MODIFIER_IGNORING_SKILL_BEFORE_ROLL.getFrom(source, jsonObject));
		String statusString = IServerJsonOption.STATUS.getFrom(source, jsonObject);
		if (StringTool.isProvided(statusString)) {
			status = ActionStatus.valueOf(statusString);
		}
		dtRerollAsked = IServerJsonOption.DT_REROLL_ASKED.getFrom(source, jsonObject);
		modifierChoiceOffered = toPrimitive(IServerJsonOption.MODIFIER_CHOICE_OFFERED.getFrom(source, jsonObject));
		freeModifiersOptional = IServerJsonOption.FREE_MODIFIERS_OPTIONAL.getFrom(source, jsonObject);
		SkillFactory skillFactory = source.getFactory(FactoryType.Factory.SKILL);
		selectedModifierSkills.clear();
		readSkills(skillFactory, IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.getFrom(source, jsonObject),
			selectedModifierSkills);
		declinedFreeSkills.clear();
		readSkills(skillFactory, IServerJsonOption.DECLINED_AGILITY_MODIFIER_SKILLS.getFrom(source, jsonObject),
			declinedFreeSkills);
		String pendingSkillName = IServerJsonOption.PENDING_AGILITY_MODIFIER_SKILL.getFrom(source, jsonObject);
		pendingFreeSkill = StringTool.isProvided(pendingSkillName) ? skillFactory.forName(pendingSkillName) : null;
		return this;
	}

	private void readSkills(SkillFactory skillFactory, JsonArray skillNames, Set<Skill> target) {
		if (skillNames == null) {
			return;
		}
		for (JsonValue skillName : skillNames) {
			target.add(skillFactory.forName(skillName.asString()));
		}
	}

}
