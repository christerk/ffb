package com.fumbbl.ffb.server.step.bb2025.move;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.ReRollOptions;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.DodgeModifierFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandReRollModifierChoice;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.mixed.ReportDodgeRoll;
import com.fumbbl.ffb.report.mixed.ReportModifiedDodgeResultSuccessful;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeDropDodge;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeDropDodgeForSpp;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeServer;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
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
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.server.util.bb2025.DodgeModifierSelectionService;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Step in move sequence to handle skill DODGE.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * <p>
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step. Expects
 * stepParameter COORDINATE_TO to be set by a preceding step. Expects
 * stepParameter SELECTED_DODGE_MODIFIER_SKILLS to be set by a preceding step.
 * Expects stepParameter USING_DIVING_TACKLE to be set by a preceding step.
 * <p>
 * StepParameter RE_ROLL_USED may be set by a preceding step. StepParameter
 * DODGE_ROLL may be set by a preceding step.
 * <p>
 * Sets stepParameter RE_ROLL_USED for all steps on the stack. Sets
 * stepParameter DODGE_ROLL for all steps on the stack. Sets stepParameter
 * INJURY_TYPE for all steps on the stack. Sets stepParameter
 * SELECTED_DODGE_MODIFIER_SKILLS for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepMoveDodge extends AbstractStepWithReRoll {

	private final DodgeModifierSelectionService selectionService = new DodgeModifierSelectionService();

	private String fGotoLabelOnFailure;
	private FieldCoordinate fCoordinateFrom;
	private FieldCoordinate fCoordinateTo;
	private int fDodgeRoll;
	private Boolean fUsingDivingTackle;
	private boolean fReRollUsed;
	private Boolean usingModifierIgnoringSkill;
	private Set<DodgeModifier> dodgeModifiers = new HashSet<>();
	private final Set<Skill> selectedModifierSkills = new LinkedHashSet<>();
	private Player<?>[] armBarPlayers;
	private String armBarPlayerId;
	private boolean armBarChoice;
	private boolean modifierChoiceOffered;
	private List<Skill> chosenModifierSkills;

	public StepMoveDodge(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.MOVE_DODGE;
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
	@SuppressWarnings("unchecked")
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case COORDINATE_FROM:
					fCoordinateFrom = (FieldCoordinate) parameter.getValue();
					return true;
				case COORDINATE_TO:
					fCoordinateTo = (FieldCoordinate) parameter.getValue();
					return true;
				case DODGE_ROLL:
					fDodgeRoll = (Integer) parameter.getValue();
					return true;
				case USING_DIVING_TACKLE:
					fUsingDivingTackle = (Boolean) parameter.getValue();
					return true;
				case SELECTED_DODGE_MODIFIER_SKILLS:
					selectedModifierSkills.clear();
					if (parameter.getValue() != null) {
						selectedModifierSkills.addAll((Set<Skill>) parameter.getValue());
					}
					return true;
				case MODIFIER_CHOICE_OFFERED:
					modifierChoiceOffered = parameter.getValue() != null && (Boolean) parameter.getValue();
					return true;
				case RE_ROLL_USED:
					fReRollUsed = parameter.getValue() != null && (Boolean) parameter.getValue();
					return true;
				default:
					break;
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND &&
			pReceivedCommand.getId() == NetCommandId.CLIENT_RE_ROLL_MODIFIER_CHOICE) {
			ClientCommandReRollModifierChoice modifierChoiceCommand =
				(ClientCommandReRollModifierChoice) pReceivedCommand.getCommand();
			chosenModifierSkills = new ArrayList<>(modifierChoiceCommand.getSkills());
			commandStatus = StepCommandStatus.EXECUTE_STEP;
		}
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND &&
			pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
			ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
			if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canChooseToIgnoreDodgeModifierAfterRoll)) {
				usingModifierIgnoringSkill = commandUseSkill.isSkillUsed();
				if (!usingModifierIgnoringSkill) {
					setReRollSource(findSkillReRollSource(ReRolledActions.DODGE));
				}
				commandStatus = StepCommandStatus.EXECUTE_STEP;
			} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canRerollDodge)) {
				setReRolledAction(ReRolledActions.DODGE);
				setReRollSource(commandUseSkill.getSkill().getRerollSource(ReRolledActions.DODGE));
				commandStatus = StepCommandStatus.EXECUTE_STEP;
			} else {
				commandStatus =
					handleSkillCommand((ClientCommandUseSkill) pReceivedCommand.getCommand(), getGameState().getPassState());
			}
		}
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND &&
			pReceivedCommand.getId() == NetCommandId.CLIENT_PLAYER_CHOICE) {
			ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
			if (playerChoiceCommand.getPlayerChoiceMode() == PlayerChoiceMode.ARM_BAR) {
				armBarPlayerId = playerChoiceCommand.getPlayerId();
				armBarChoice = true;
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
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.isDodging()) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		if (chosenModifierSkills != null) {
			List<Skill> skills = chosenModifierSkills;
			chosenModifierSkills = null;
			commitModifierSkills(skills);
			handleStatus(dodge(false));
			return;
		}

		if (ReRolledActions.DODGE == getReRolledAction() && !Boolean.TRUE.equals(usingModifierIgnoringSkill)
			&& !(modifierChoiceOffered && getReRollSource() == null)) {
			if (getReRollSource() == null || armBarChoice) {
				failDodge();
				return;
			} else if (!UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
				if (modifierChoiceOffered) {
					// the loner roll failed, keep evaluating the die that was already rolled
					setReRollSource(null);
				} else {
					failDodge();
					return;
				}
			} else {
				fReRollUsed = true;
			}
		}

		boolean reRolledAction = getReRolledAction() == ReRolledActions.DODGE && getReRollSource() != null;
		boolean doRoll = (reRolledAction || (fUsingDivingTackle == null && !modifierChoiceOffered))
			&& !Boolean.TRUE.equals(usingModifierIgnoringSkill);
		handleStatus(dodge(doRoll));
	}

	private void handleStatus(ActionStatus status) {
		Game game = getGameState().getGame();
		switch (status) {
			case SUCCESS:
				boolean reRolledAction = (getReRolledAction() == ReRolledActions.DODGE) && (getReRollSource() != null);
				publishParameter(new StepParameter(StepParameterKey.RE_ROLL_USED, fReRollUsed || reRolledAction));
				getResult().setNextAction(StepAction.NEXT_STEP);
				break;
			case FAILURE:
				if (UtilGameOption.isOptionEnabled(game, GameOptionId.STAND_FIRM_NO_DROP_ON_FAILED_DODGE)) {
					publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
					getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					failDodge();
				}
				break;
			case WAITING_FOR_RE_ROLL:
			case WAITING_FOR_SKILL_USE:
				getResult().setNextAction(StepAction.CONTINUE);
				break;
			default:
				break;
		}
	}

	private void failDodge() {

		Game game = getGameState().getGame();
		if (!ArrayTool.isProvided(armBarPlayers)) {
			armBarPlayers = UtilPlayer.findAdjacentOpposingPlayersWithProperty(game, fCoordinateFrom,
				NamedProperties.affectsEitherArmourOrInjuryOnDodge, true);
			armBarPlayers = UtilPlayer.filterThrower(game, armBarPlayers);
		}

		Player<?> armBarPlayer = null;
		if (StringTool.isProvided(armBarPlayerId)) {
			armBarPlayer = game.getPlayerById(armBarPlayerId);
		} else if (!armBarChoice && ArrayTool.isProvided(armBarPlayers)) {
			if (armBarPlayers.length == 1) {
				armBarPlayer = armBarPlayers[0];
			} else {
				String teamId = game.getOtherTeam(game.getActingTeam()).getId();
				UtilServerDialog.showDialog(getGameState(),
					new DialogPlayerChoiceParameter(teamId, PlayerChoiceMode.ARM_BAR, armBarPlayers, null, 1), true);
				getResult().setNextAction(StepAction.CONTINUE);
				return;
			}
		}

		InjuryTypeServer<?> injuryType = (armBarPlayer != null)
			? new InjuryTypeDropDodgeForSpp(armBarPlayer)
			: new InjuryTypeDropDodge(false);

		publishParameter(new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT, new SteadyFootingContext(injuryType)));
		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);

	}

	private ActionStatus dodge(boolean pDoRoll) {

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		AgilityMechanic mechanic =
			(AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());

		if (pDoRoll) {
			publishParameter(new StepParameter(StepParameterKey.DODGE_ROLL, getGameState().getDiceRoller().rollSkill()));
			// a fresh die may be improved by the very same skills again
			setModifierChoiceOffered(false);
		}

		int minimumRollWithoutModifiers = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), Collections.emptySet());
		boolean successWithoutModifiers =
			DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRollWithoutModifiers);
		Optional<Skill> ignoreModifierSkill = Optional.empty();

		if (successWithoutModifiers) {
			ignoreModifierSkill = Optional.ofNullable(
				UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canChooseToIgnoreDodgeModifierAfterRoll));
			if (!ignoreModifierSkill.isPresent()) {
				successWithoutModifiers = false;
			}
			if (Boolean.TRUE.equals(usingModifierIgnoringSkill) && ignoreModifierSkill.isPresent()) {
				actingPlayer.markSkillUsed(ignoreModifierSkill.get());
				getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), ignoreModifierSkill.get(), true,
					SkillUse.PASS_DODGE_WITHOUT_MODIFIERS));
				return ActionStatus.SUCCESS;
			}

			if (Boolean.FALSE.equals(usingModifierIgnoringSkill) && getReRollSource() == null
				&& getReRolledAction() != null) {
				return ActionStatus.FAILURE;
			}
		}

		DodgeModifierFactory modifierFactory = game.getFactory(Factory.DODGE_MODIFIER);
		dodgeModifiers = modifierFactory.findModifiers(
			new DodgeContext(game, actingPlayer, fCoordinateFrom, fCoordinateTo, selectedModifierSkills));
		if (Boolean.TRUE.equals(fUsingDivingTackle)) {
			dodgeModifiers.addAll(modifierFactory.forType(ModifierType.DIVING_TACKLE));
		}

		int minimumRoll = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers);
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRoll);

		if (pDoRoll) {
			boolean reRolled = getReRolledAction() == ReRolledActions.DODGE && getReRollSource() != null;
			getResult().addReport(new ReportDodgeRoll(actingPlayer.getPlayerId(), successful, fDodgeRoll, minimumRoll,
				reRolled, dodgeModifiers.toArray(new DodgeModifier[0]), null));
		}

		if (successful) {
			return divingTackleLookAhead(mechanic, modifierFactory, successWithoutModifiers, ignoreModifierSkill);
		}

		return offerRescue(dodgeModifiers, minimumRoll, false, successWithoutModifiers, ignoreModifierSkill);
	}

	/**
	 * The dodge succeeded on its own, but an opponent may still declare Diving Tackle. When that would turn the dodge
	 * into a failure the coach gets the same options as on a real failure, because all modifiers of the dodging player
	 * have to be declared before Diving Tackle is decided.
	 */
	private ActionStatus divingTackleLookAhead(AgilityMechanic mechanic, DodgeModifierFactory modifierFactory,
																						 boolean successWithoutModifiers, Optional<Skill> ignoreModifierSkill) {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (fUsingDivingTackle != null || modifierChoiceOffered) {
			return ActionStatus.SUCCESS;
		}

		Player<?>[] divingTacklers = UtilPlayer.findEligibleDivingTacklers(game, fCoordinateFrom, fCoordinateTo,
			NamedProperties.canAttemptToTackleDodgingPlayer);
		if (!ArrayTool.isProvided(divingTacklers)) {
			return ActionStatus.SUCCESS;
		}

		Set<DodgeModifier> withDivingTackle = new HashSet<>(dodgeModifiers);
		withDivingTackle.addAll(modifierFactory.forType(ModifierType.DIVING_TACKLE));
		int minimumRollWithDivingTackle = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), withDivingTackle);
		if (DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRollWithDivingTackle)) {
			return ActionStatus.SUCCESS;
		}

		return offerRescue(withDivingTackle, minimumRollWithDivingTackle, true, successWithoutModifiers,
			ignoreModifierSkill);
	}

	/**
	 * Single entry point for offering optional dodge modifiers and/or a re-roll to the coach.
	 *
	 * @param extraModifiers    the modifiers applying to the roll that is being rescued, including Diving Tackle when
	 *                          the look ahead triggered this call
	 * @param minimumRoll       the roll that would have been needed with those modifiers
	 * @param dueToDivingTackle when true the dodge itself succeeded, so declining everything keeps the success
	 */
	private ActionStatus offerRescue(Set<DodgeModifier> extraModifiers, int minimumRoll, boolean dueToDivingTackle,
																	 boolean successWithoutModifiers, Optional<Skill> ignoreModifierSkill) {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		ActionStatus fallback = dueToDivingTackle ? ActionStatus.SUCCESS : ActionStatus.FAILURE;

		if (modifierChoiceOffered || armBarChoice) {
			return fallback;
		}

		List<ModifierChoiceOption> options = selectionService.findOptions(game, actingPlayer, fCoordinateFrom,
			fCoordinateTo, extraModifiers, fDodgeRoll);

		boolean reRollPossible = fUsingDivingTackle == null && !fReRollUsed
			&& (getReRolledAction() != ReRolledActions.DODGE
			|| (successWithoutModifiers && !Boolean.TRUE.equals(usingModifierIgnoringSkill)));

		ReRollSource skillReRollSource = reRollPossible ? uncanceledDodgeRerollSource(game, actingPlayer) : null;

		if (!options.isEmpty()) {
			RollMechanic rollMechanic =
				(RollMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());
			ReRollOptions reRollOptions = reRollPossible
				? rollMechanic.findReRollOptions(getGameState(), actingPlayer.getPlayer(), ReRolledActions.DODGE,
				skillReRollSource != null ? skillReRollSource.getSkill(game) : null)
				: new ReRollOptions(new ArrayList<>(), null);

			List<String> messages = new ArrayList<>();
			if (dueToDivingTackle) {
				messages.add("Diving Tackle can make this dodge fail.");
			}

			if (!dueToDivingTackle && reRollPossible) {
				setReRolledAction(ReRolledActions.DODGE);
			}

			Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			UtilServerDialog.showDialog(getGameState(),
				new DialogReRollModifierChoiceParameter(actingPlayer.getPlayerId(), ReRolledActions.DODGE, minimumRoll,
					fDodgeRoll, options, reRollOptions.getProperties(), false, reRollOptions.getReRollSkill(), null, null,
					messages),
				!actingTeam.hasPlayer(actingPlayer.getPlayer()));
			setModifierChoiceOffered(true);
			return ActionStatus.WAITING_FOR_RE_ROLL;
		}

		if (!reRollPossible) {
			if (ignoreModifierSkill.isPresent() && usingModifierIgnoringSkill == null) {
				return offerModifierIgnoringSkill(ignoreModifierSkill.get());
			}
			return fallback;
		}

		if (!dueToDivingTackle) {
			setReRolledAction(ReRolledActions.DODGE);
			if (ignoreModifierSkill.isPresent() && usingModifierIgnoringSkill == null) {
				return offerModifierIgnoringSkill(ignoreModifierSkill.get());
			}
			if (skillReRollSource != null) {
				fReRollUsed = true;
				useSkillReRollSource(skillReRollSource);
				return dodge(true);
			}
		}

		List<String> messages = dueToDivingTackle
			? Collections.singletonList("Diving Tackle can make this dodge fail. Reroll the dodge now?")
			: null;
		Skill reRollSkill = skillReRollSource != null ? skillReRollSource.getSkill(game) : null;

		if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledActions.DODGE,
			minimumRoll, false, null, reRollSkill, null, null, messages)) {
			if (dueToDivingTackle) {
				setModifierChoiceOffered(true);
			}
			return ActionStatus.WAITING_FOR_RE_ROLL;
		}

		return fallback;
	}

	private ActionStatus offerModifierIgnoringSkill(Skill ignoreModifierSkill) {
		ActingPlayer actingPlayer = getGameState().getGame().getActingPlayer();
		getResult().addReport(new ReportModifiedDodgeResultSuccessful(ignoreModifierSkill));
		UtilServerDialog.showDialog(getGameState(),
			new DialogSkillUseParameter(actingPlayer.getPlayerId(), ignoreModifierSkill, 0), false);
		usingModifierIgnoringSkill = null;
		return ActionStatus.WAITING_FOR_SKILL_USE;
	}

	private void commitModifierSkills(List<Skill> skills) {
		ActingPlayer actingPlayer = getGameState().getGame().getActingPlayer();
		for (Skill skill : skills) {
			if (skill != null && selectedModifierSkills.add(skill)) {
				actingPlayer.markSkillUsed(skill);
				getResult()
					.addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.ADD_DODGE_MODIFIER));
			}
		}
		publishParameter(new StepParameter(StepParameterKey.SELECTED_DODGE_MODIFIER_SKILLS,
			new LinkedHashSet<>(selectedModifierSkills)));
	}

	private void setModifierChoiceOffered(boolean offered) {
		modifierChoiceOffered = offered;
		publishParameter(new StepParameter(StepParameterKey.MODIFIER_CHOICE_OFFERED, offered));
	}

	private void useSkillReRollSource(ReRollSource skillRerollSource) {
		ActingPlayer actingPlayer = getGameState().getGame().getActingPlayer();
		setReRollSource(skillRerollSource);
		UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer());
	}

	private ReRollSource uncanceledDodgeRerollSource(Game game, ActingPlayer actingPlayer) {
		ReRollSource source = findSkillReRollSource(ReRolledActions.DODGE);
		if (source != null) {
			Team otherTeam = UtilPlayer.findOtherTeam(game, actingPlayer.getPlayer());
			Player<?>[] opponents = UtilPlayer.findAdjacentPlayersWithTacklezones(game, otherTeam, fCoordinateFrom, false);
			for (Player<?> opponent : opponents) {
				if (UtilCards.cancelsSkill(opponent, source.getSkill(game))) {
					return null;
				}
			}
		}
		return source;
	}

	@Override
	protected ReRollSource findSkillReRollSource(ReRolledAction reRolledAction) {
		Game game = getGameState().getGame();
		ReRollSource skillRerollSource = null;
		if (TurnMode.REGULAR == game.getTurnMode() || game.getTurnMode() == TurnMode.BLITZ) {
			skillRerollSource = UtilCards.getUnusedRerollSource(game.getActingPlayer(), reRolledAction);
		}
		return skillRerollSource;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, fCoordinateTo);
		IServerJsonOption.DODGE_ROLL.addTo(jsonObject, fDodgeRoll);
		IServerJsonOption.USING_DIVING_TACKLE.addTo(jsonObject, fUsingDivingTackle);
		IServerJsonOption.RE_ROLL_USED.addTo(jsonObject, fReRollUsed);
		IServerJsonOption.USING_MODIFIER_IGNORING_SKILL.addTo(jsonObject, usingModifierIgnoringSkill);
		JsonArray modifierArray = new JsonArray();
		dodgeModifiers.stream().map(UtilJson::toJsonValue).forEach(modifierArray::add);
		IServerJsonOption.ROLL_MODIFIERS.addTo(jsonObject, modifierArray);
		JsonArray skillArray = new JsonArray();
		selectedModifierSkills.stream().map(UtilJson::toJsonValue).forEach(skillArray::add);
		IServerJsonOption.SELECTED_DODGE_MODIFIER_SKILLS.addTo(jsonObject, skillArray);
		IServerJsonOption.ARM_BAR_PLAYER_ID.addTo(jsonObject, armBarPlayerId);
		IServerJsonOption.ARM_BAR_CHOICE.addTo(jsonObject, armBarChoice);
		IServerJsonOption.MODIFIER_CHOICE_OFFERED.addTo(jsonObject, modifierChoiceOffered);
		return jsonObject;
	}

	@Override
	public StepMoveDodge initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(source, jsonObject);
		fCoordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(source, jsonObject);
		fDodgeRoll = IServerJsonOption.DODGE_ROLL.getFrom(source, jsonObject);
		fUsingDivingTackle = IServerJsonOption.USING_DIVING_TACKLE.getFrom(source, jsonObject);
		fReRollUsed = toPrimitive(IServerJsonOption.RE_ROLL_USED.getFrom(source, jsonObject));
		usingModifierIgnoringSkill = IServerJsonOption.USING_MODIFIER_IGNORING_SKILL.getFrom(source, jsonObject);
		JsonArray modifierArray = IJsonOption.ROLL_MODIFIERS.getFrom(source, jsonObject);
		if (modifierArray != null) {
			DodgeModifierFactory modifierFactory = source.getFactory(Factory.DODGE_MODIFIER);
			if (modifierFactory != null) {
				for (int i = 0; i < modifierArray.size(); i++) {
					dodgeModifiers.add((DodgeModifier) UtilJson.toEnumWithName(modifierFactory, modifierArray.get(i)));
				}
			}
		}
		selectedModifierSkills.clear();
		JsonArray skillArray = IServerJsonOption.SELECTED_DODGE_MODIFIER_SKILLS.getFrom(source, jsonObject);
		if (skillArray != null) {
			SkillFactory skillFactory = source.getFactory(Factory.SKILL);
			if (skillFactory != null) {
				for (int i = 0; i < skillArray.size(); i++) {
					selectedModifierSkills.add((Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i)));
				}
			}
		}
		armBarPlayerId = IServerJsonOption.ARM_BAR_PLAYER_ID.getFrom(source, jsonObject);
		armBarChoice = IServerJsonOption.ARM_BAR_CHOICE.getFrom(source, jsonObject);
		modifierChoiceOffered = toPrimitive(IServerJsonOption.MODIFIER_CHOICE_OFFERED.getFrom(source, jsonObject));
		return this;
	}

}
