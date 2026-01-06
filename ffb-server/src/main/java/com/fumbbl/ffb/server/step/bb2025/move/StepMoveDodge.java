package com.fumbbl.ffb.server.step.bb2025.move;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.DodgeModifierFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.StatBasedRollModifier;
import com.fumbbl.ffb.net.NetCommandId;
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
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Step in move sequence to handle skill DODGE.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * <p>
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step. Expects
 * stepParameter COORDINATE_TO to be set by a preceding step. Expects
 * stepParameter USING_BREAK_TACKLE to be set by a preceding step. Expects
 * stepParameter USING_DIVING_TACKLE to be set by a preceding step.
 * <p>
 * StepParameter RE_ROLL_USED may be set by a preceding step. StepParameter
 * DODGE_ROLL may be set by a preceding step.
 * <p>
 * Sets stepParameter RE_ROLL_USED for all steps on the stack. Sets
 * stepParameter DODGE_ROLL for all steps on the stack. Sets stepParameter
 * INJURY_TYPE for all steps on the stack. Sets stepParameter USING_BREAK_TACKLE
 * for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepMoveDodge extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure;
	private FieldCoordinate fCoordinateFrom;
	private FieldCoordinate fCoordinateTo;
	private int fDodgeRoll;
	private Boolean fUsingDivingTackle;
	private boolean fUsingBreakTackle;
	private boolean fReRollUsed;
	private Boolean usingModifyingSkill;
	private Boolean usingModifierIgnoringSkill;
	private Set<DodgeModifier> dodgeModifiers = new HashSet<>();
	private Player<?>[] armBarPlayers;
	private String armBarPlayerId;
	private boolean armBarChoice;

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
				case USING_BREAK_TACKLE:
					fUsingBreakTackle = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					return true;
				case USING_DIVING_TACKLE:
					fUsingDivingTackle = (Boolean) parameter.getValue();
					return true;
				case USING_MODIFYING_SKILL:
					usingModifyingSkill = (Boolean) parameter.getValue();
					return true;
				case RE_ROLL_USED:
					fReRollUsed = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
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
			pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
			ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
			if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canAddStrengthToDodge)) {
				usingModifyingSkill = commandUseSkill.isSkillUsed();
				if (!usingModifyingSkill) {
					ReRollSource skillRerollSource = findSkillReRollSource(ReRolledActions.DODGE);
					if (skillRerollSource != null) {
						useSkillReRollSource(skillRerollSource);
					}
				}
				commandStatus = StepCommandStatus.EXECUTE_STEP;
			} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canChooseToIgnoreDodgeModifierAfterRoll)) {
				usingModifierIgnoringSkill = commandUseSkill.isSkillUsed();
				if (!usingModifierIgnoringSkill) {
					setReRollSource(findSkillReRollSource(ReRolledActions.DODGE));
				}
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
		if (ReRolledActions.DODGE == getReRolledAction() && !Boolean.TRUE.equals(usingModifierIgnoringSkill)) {
			if (usingModifyingSkill == null) {
				if (getReRollSource() == null) {
					failDodge();
					return;
				} else if (!UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
					AgilityMechanic mechanic =
						(AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
					if (usingModifyingSkill != null || !showUseModifyingSkillDialog(mechanic, dodgeModifiers) ||
						!Boolean.TRUE.equals(usingModifierIgnoringSkill)) {
						failDodge();
					}
					return;
				} else {
					fReRollUsed = true;
				}
			}
		}
		boolean reRolledAction = getReRolledAction() == ReRolledActions.DODGE && getReRollSource() != null;
		boolean doRoll = (reRolledAction || (fUsingDivingTackle == null)) && !Boolean.TRUE.equals(usingModifyingSkill) &&
			!Boolean.TRUE.equals(usingModifierIgnoringSkill);
		switch (dodge(doRoll)) {
			case SUCCESS:
				reRolledAction = (getReRolledAction() == ReRolledActions.DODGE) && (getReRollSource() != null);
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
				String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
				UtilServerDialog.showDialog(getGameState(),
					new DialogPlayerChoiceParameter(teamId, PlayerChoiceMode.ARM_BAR, armBarPlayers, null, 1), true);
				getResult().setNextAction(StepAction.CONTINUE);
				return;
			}
		}
		
		InjuryTypeServer<?> injuryType = (armBarPlayer != null)
			? new InjuryTypeDropDodgeForSpp(armBarPlayer)
			: new InjuryTypeDropDodge(false);

		publishParameter(new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT,	new SteadyFootingContext(injuryType)));
		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);

	}

	private ActionStatus dodge(boolean pDoRoll) {

		ActionStatus status;
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		AgilityMechanic mechanic =
			(AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());

		if (pDoRoll) {
			publishParameter(new StepParameter(StepParameterKey.DODGE_ROLL, getGameState().getDiceRoller().rollSkill()));
		}

		int minimumRollWithoutModifiers = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(),
			Collections.emptySet());
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

			if (Boolean.FALSE.equals(usingModifierIgnoringSkill) && getReRollSource() == null &&
				getReRolledAction() != null) {
				return ActionStatus.FAILURE;
			}
		}


		DodgeModifierFactory modifierFactory = game.getFactory(Factory.DODGE_MODIFIER);
		dodgeModifiers = modifierFactory.findModifiers(
			new DodgeContext(game, actingPlayer, fCoordinateFrom, fCoordinateTo, fUsingBreakTackle));
		if ((fUsingDivingTackle != null) && fUsingDivingTackle) {
			dodgeModifiers.addAll(modifierFactory.forType(ModifierType.DIVING_TACKLE));
		}


		StatBasedRollModifier statBasedRollModifier = null;

		if (Boolean.TRUE.equals(usingModifyingSkill)) {
			statBasedRollModifier = actingPlayer.statBasedModifier(NamedProperties.canAddStrengthToDodge);
			UtilCards.getSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canAddStrengthToDodge)
				.ifPresent(modifyingSkill -> {
					actingPlayer.markSkillUsed(NamedProperties.canAddStrengthToDodge);
					getResult().addReport(
						new ReportSkillUse(actingPlayer.getPlayerId(), modifyingSkill, true, SkillUse.ADD_STRENGTH_TO_ROLL));
					publishParameter(StepParameter.from(StepParameterKey.USING_MODIFYING_SKILL, true));
				});
		}

		int minimumRoll = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers, statBasedRollModifier);
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRoll);

		Optional<DodgeModifier> btModifier = dodgeModifiers.stream().filter(DodgeModifier::isUseStrength).findFirst();

		Optional<Skill> btSkill = actingPlayer.getPlayer().getSkillsIncludingTemporaryOnes().stream()
			.filter(skill -> btModifier.isPresent() && skill.getDodgeModifiers().contains(btModifier.get())).findFirst();

		Skill modifyingSkill = null;

		if (successful) {
			if (btModifier.isPresent()) {
				dodgeModifiers.remove(btModifier.get());
				int minimumRollWithoutBreakTackle =
					mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers, statBasedRollModifier);
				if (!DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRollWithoutBreakTackle)) {
					dodgeModifiers.add(btModifier.get());
				} else {
					minimumRoll = minimumRollWithoutBreakTackle;
				}
			}
		} else {
			modifyingSkill = getModifyingSkillInCaseItHelps(mechanic, dodgeModifiers, false);
			if (pDoRoll) {
				if (btModifier.isPresent()) {
					if (modifyingSkill != null) {
						dodgeModifiers.remove(btModifier.get());
						int minimumRollWithoutBreakTackle =
							mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers,
								actingPlayer.statBasedModifier(NamedProperties.canAddStrengthToDodge));
						if (!DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRollWithoutBreakTackle)) {
							dodgeModifiers.add(btModifier.get());
						}

					} else {
						dodgeModifiers.remove(btModifier.get());
						minimumRoll = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers);
						if (!fUsingBreakTackle && btSkill.isPresent()) {
							getResult().addReport(new ReportSkillUse(null, btSkill.get(), false, SkillUse.WOULD_NOT_HELP));
						}
					}
				}
			}
		}

		boolean reRolled = ((getReRolledAction() == ReRolledActions.DODGE) && (getReRollSource() != null));
		getResult().addReport(
			new ReportDodgeRoll(actingPlayer.getPlayerId(), successful, (pDoRoll ? fDodgeRoll : 0), minimumRoll, reRolled,
				dodgeModifiers.toArray(new DodgeModifier[0]), statBasedRollModifier));


		if (successful) {
			status = ActionStatus.SUCCESS;
		} else {
			status = ActionStatus.FAILURE;

			if (!fReRollUsed && (getReRolledAction() != ReRolledActions.DODGE ||
				(successWithoutModifiers && !Boolean.TRUE.equals(usingModifierIgnoringSkill)))) {
				setReRolledAction(ReRolledActions.DODGE);
				ReRollSource skillRerollSource = findSkillReRollSource(ReRolledActions.DODGE);
				if (skillRerollSource != null) {
					Team otherTeam = UtilPlayer.findOtherTeam(game, actingPlayer.getPlayer());
					Player<?>[] opponents =
						UtilPlayer.findAdjacentPlayersWithTacklezones(game, otherTeam, fCoordinateFrom, false);
					for (Player<?> opponent : opponents) {
						if (UtilCards.cancelsSkill(opponent, skillRerollSource.getSkill(game))) {
							skillRerollSource = null;
							break;
						}
					}
				}
				if (skillRerollSource != null) {
					if (modifyingSkill != null && usingModifyingSkill == null) {
						getResult().addReport(new ReportModifiedDodgeResultSuccessful(modifyingSkill));
						status = ActionStatus.WAITING_FOR_RE_ROLL;
						UtilServerDialog.showDialog(getGameState(),
							new DialogSkillUseParameter(actingPlayer.getPlayerId(), modifyingSkill, 0), false);
					} else if (ignoreModifierSkill.isPresent()) {
						getResult().addReport(new ReportModifiedDodgeResultSuccessful(ignoreModifierSkill.get()));
						status = ActionStatus.WAITING_FOR_SKILL_USE;
						UtilServerDialog.showDialog(getGameState(),
							new DialogSkillUseParameter(actingPlayer.getPlayerId(), ignoreModifierSkill.get(), 0), false);
						usingModifierIgnoringSkill = null;
					} else {
						fReRollUsed = true;
						useSkillReRollSource(skillRerollSource);
						status = dodge(true);
					}
				} else {
					ActionStatus statusToSet = ActionStatus.WAITING_FOR_RE_ROLL;
					if (modifyingSkill == null && ignoreModifierSkill.isPresent()) {
						modifyingSkill = ignoreModifierSkill.get();
						statusToSet = ActionStatus.WAITING_FOR_SKILL_USE;
					}

					if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledActions.DODGE,
						minimumRoll, false, modifyingSkill, null)) {
						if (modifyingSkill != null) {
							getResult().addReport(new ReportModifiedDodgeResultSuccessful(modifyingSkill));
							if (statusToSet == ActionStatus.WAITING_FOR_SKILL_USE) {
								usingModifierIgnoringSkill = null;
							}
						}
						status = statusToSet;
					}
				}
			} else if (modifyingSkill != null) {
				getResult().addReport(new ReportModifiedDodgeResultSuccessful(modifyingSkill));
				status = ActionStatus.WAITING_FOR_RE_ROLL;
				UtilServerDialog.showDialog(getGameState(),
					new DialogSkillUseParameter(actingPlayer.getPlayerId(), modifyingSkill, minimumRoll), false);
			} else if (ignoreModifierSkill.isPresent()) {
				getResult().addReport(new ReportModifiedDodgeResultSuccessful(ignoreModifierSkill.get()));
				status = ActionStatus.WAITING_FOR_SKILL_USE;
				UtilServerDialog.showDialog(getGameState(),
					new DialogSkillUseParameter(actingPlayer.getPlayerId(), ignoreModifierSkill.get(), 0), false);
				usingModifierIgnoringSkill = null;
			}
		}

		if (btSkill.isPresent() && dodgeModifiers.stream().anyMatch(DodgeModifier::isUseStrength) &&
			((status == ActionStatus.SUCCESS))) {
			fUsingBreakTackle = true;
			actingPlayer.markSkillUsed(btSkill.get());
			publishParameter(new StepParameter(StepParameterKey.USING_BREAK_TACKLE, fUsingBreakTackle));
		}

		return status;

	}

	private void useSkillReRollSource(ReRollSource skillRerollSource) {
		ActingPlayer actingPlayer = getGameState().getGame().getActingPlayer();
		setReRollSource(skillRerollSource);
		UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer());
	}

	private boolean showUseModifyingSkillDialog(AgilityMechanic mechanic, Set<DodgeModifier> dodgeModifiers) {
		if (usingModifyingSkill == null) {
			Skill modifyingSkill = getModifyingSkillInCaseItHelps(mechanic, dodgeModifiers, true);
			if (modifyingSkill != null) {
				UtilServerDialog.showDialog(getGameState(),
					new DialogSkillUseParameter(getGameState().getGame().getActingPlayer().getPlayerId(), modifyingSkill, 0),
					false);
				return true;
			}
		}
		return false;
	}

	private Skill getModifyingSkillInCaseItHelps(AgilityMechanic mechanic, Set<DodgeModifier> dodgeModifiers,
																							 boolean addReport) {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Skill modifyingSkill = null;

		int minimumRoll = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers,
			actingPlayer.statBasedModifier(NamedProperties.canAddStrengthToDodge));
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRoll);

		if (successful) {
			modifyingSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canAddStrengthToDodge);
			if (addReport) {
				getResult().addReport(new ReportModifiedDodgeResultSuccessful(modifyingSkill));
			}
		}

		return modifyingSkill;
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
		IServerJsonOption.USING_BREAK_TACKLE.addTo(jsonObject, fUsingBreakTackle);
		IServerJsonOption.RE_ROLL_USED.addTo(jsonObject, fReRollUsed);
		IServerJsonOption.USING_MODIFYING_SKILL.addTo(jsonObject, usingModifyingSkill);
		IServerJsonOption.USING_MODIFIER_IGNORING_SKILL.addTo(jsonObject, usingModifierIgnoringSkill);
		JsonArray modifierArray = new JsonArray();
		dodgeModifiers.stream().map(UtilJson::toJsonValue).forEach(modifierArray::add);
		IServerJsonOption.ROLL_MODIFIERS.addTo(jsonObject, modifierArray);
		IServerJsonOption.ARM_BAR_PLAYER_ID.addTo(jsonObject, armBarPlayerId);
		IServerJsonOption.ARM_BAR_CHOICE.addTo(jsonObject, armBarChoice);
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
		fUsingBreakTackle = IServerJsonOption.USING_BREAK_TACKLE.getFrom(source, jsonObject);
		fReRollUsed = toPrimitive(IServerJsonOption.RE_ROLL_USED.getFrom(source, jsonObject));
		usingModifyingSkill = IServerJsonOption.USING_MODIFYING_SKILL.getFrom(source, jsonObject);
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
		armBarPlayerId = IServerJsonOption.ARM_BAR_PLAYER_ID.getFrom(source, jsonObject);
		armBarChoice = IServerJsonOption.ARM_BAR_CHOICE.getFrom(source, jsonObject);
		return this;
	}

}
