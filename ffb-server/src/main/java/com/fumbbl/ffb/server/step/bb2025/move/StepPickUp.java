package com.fumbbl.ffb.server.step.bb2025.move;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.PickupModifierFactory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.PickupContext;
import com.fumbbl.ffb.modifiers.PickupModifier;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPickUpChoice;
import com.fumbbl.ffb.net.commands.ClientCommandReRollModifierChoice;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.bb2025.ReportPickupRoll;
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
import com.fumbbl.ffb.server.step.bb2025.shared.StallingExtension;
import com.fumbbl.ffb.server.util.ReRollRequest;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.server.util.bb2025.PickupModifierSelectionService;
import com.fumbbl.ffb.server.util.bb2025.ReRollModifierChoiceDialogParameterFactory;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Step in block sequence to handle picking up the ball.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * <p>
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepPickUp extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure, thrownPlayerId;

	private boolean ignore, secureTheBall, optionalPickUp, attemptPickUp;
	private String overridePlayerId;
	private int pickupRoll;
	private boolean reRollUsed, awaitingRescue;
	private final Set<Skill> selectedModifierSkills = new LinkedHashSet<>();
	private final PickupModifierSelectionService selectionService = new PickupModifierSelectionService();
	private final StallingExtension stallingExtension = new StallingExtension();

	public StepPickUp(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.PICK_UP;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					case GOTO_LABEL_ON_FAILURE:
						fGotoLabelOnFailure = (String) parameter.getValue();
						break;
					case THROWN_PLAYER_ID:
						thrownPlayerId = (String) parameter.getValue();
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
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case SKIP:
					ignore = toPrimitive((Boolean) parameter.getValue());
					return true;
				case FOLLOWUP_CHOICE:
					ignore = !toPrimitive((Boolean) parameter.getValue());
					return true;
				case PICK_UP_OPTIONAL:
					optionalPickUp = toPrimitive((Boolean) parameter.getValue());
					ignore = false;
					return true;
				case PLAYER_ON_BALL_ID:
					overridePlayerId = (String) parameter.getValue();
					return true;
				case ATTEMPT_PICK_UP:
					attemptPickUp = (Boolean) parameter.getValue();
					ignore = false;
					return true;
				default:
					break;
			}
			return false;
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
		if (pReceivedCommand.getId() == NetCommandId.CLIENT_RE_ROLL_MODIFIER_CHOICE) {
			ClientCommandReRollModifierChoice command = (ClientCommandReRollModifierChoice) pReceivedCommand.getCommand();
			Game game = getGameState().getGame();
			Player<?> player = pickupPlayer();
			if (!awaitingRescue || secureTheBall || player == null
				|| command.getReRolledAction() != ReRolledActions.PICK_UP
				|| !player.getId().equals(command.getPlayerId())
				|| !(game.getDialogParameter() instanceof DialogReRollModifierChoiceParameter)) {
				return StepCommandStatus.UNHANDLED_COMMAND;
			}
			Set<Skill> skills = new LinkedHashSet<>(command.getSkills());
			boolean offered = selectionService.findOptions(game, player, pickupRoll).stream()
				.anyMatch(option -> new LinkedHashSet<>(option.getSkills()).equals(skills));
			if (!offered || skills.size() != command.getSkills().size()) {
				return StepCommandStatus.UNHANDLED_COMMAND;
			}
			for (Skill skill : skills) {
				selectedModifierSkills.add(skill);
				if (player == game.getActingPlayer().getPlayer()) {
					game.getActingPlayer().markSkillUsed(skill);
				} else {
					player.markUsed(skill, game);
				}
				getResult().addReport(new ReportSkillUse(player.getId(), skill, true, SkillUse.ADD_AGILITY_MODIFIER));
			}
			awaitingRescue = false;
			executeStep();
			return StepCommandStatus.EXECUTE_STEP;
		}
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND &&
			pReceivedCommand.getId() == NetCommandId.CLIENT_PICK_UP_CHOICE) {
			ClientCommandPickUpChoice command = (ClientCommandPickUpChoice) pReceivedCommand.getCommand();
			attemptPickUp = command.isChoicePickUp();
			commandStatus = StepCommandStatus.EXECUTE_STEP;
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		Player<?> player = pickupPlayer();
		secureTheBall = game.getActingPlayer().getPlayerAction() == PlayerAction.SECURE_THE_BALL;
		boolean doPickUp = true;

		// Trickster optional path: coach declined; scatter already handled upstream
		if (optionalPickUp && !attemptPickUp) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		if (player != null && isPickUp(player)) {
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (playerState.hasTacklezones()) {
				if (awaitingRescue) {
					awaitingRescue = false;
					if (reRollUsed || (getReRollSource() == null)
						|| !UtilServerReRoll.useReRoll(this, getReRollSource(), player)) {
						doPickUp = false;
						publishParameter(new StepParameter(StepParameterKey.FEEDING_ALLOWED, false));
						if (!optionalPickUp) {
							publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
							getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
						} else {
							getResult().setNextAction(StepAction.NEXT_STEP);
						}
						publishParameter(
							new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.FAILED_PICK_UP));
					} else {
						reRollUsed = true;
						pickupRoll = 0;
					}
				}
				if (doPickUp) {
					switch (pickUp(player)) {
						case SUCCESS:
							game.getFieldModel().setBallMoving(false);
							getResult().setSound(SoundId.PICKUP);
							getResult().setNextAction(StepAction.NEXT_STEP);
							if (secureTheBall) {
								publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
							}
							break;
						case FAILURE:
							publishParameter(new StepParameter(StepParameterKey.FEEDING_ALLOWED, false));
							if (!optionalPickUp && !player.hasSkillProperty(NamedProperties.preventPickup)) {
								publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
								getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
							} else {
								getResult().setNextAction(StepAction.NEXT_STEP);
							}
							publishParameter(
								new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE,
									CatchScatterThrowInMode.FAILED_PICK_UP));
							break;
						default:
							break;
					}
				}
			} else {
				// a player of the own team without tackle zone was moved onto the ball with e.g. Raiding Party or some other voluntary movement (no chain pushes)
				// this should be considered a pickup fail, unless the player has e.g. Ball And Chain
				if (game.getActingTeam().hasPlayer(player) && !player.hasSkillProperty(NamedProperties.preventPickup)) {
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
				}
				publishParameter(
					new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.FAILED_PICK_UP));
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
			}
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private Player<?> pickupPlayer() {
		Game game = getGameState().getGame();
		return StringTool.isProvided(overridePlayerId) ? game.getPlayerById(overridePlayerId)
			: (StringTool.isProvided(thrownPlayerId) ? game.getPlayerById(thrownPlayerId) : game.getActingPlayer().getPlayer());
	}

	private boolean isPickUp(Player<?> player) {
		Game game = getGameState().getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		return
			!ignore
				&& game.getFieldModel().isBallInPlay()
				&& game.getFieldModel().isBallMoving()
				&& playerCoordinate.equals(game.getFieldModel().getBallCoordinate());
	}

	private ActionStatus pickUp(Player<?> player) {
		Game game = getGameState().getGame();
		if (player.hasSkillProperty(NamedProperties.preventHoldBall) ||
			player.hasSkillProperty(NamedProperties.preventPickup)) {
			return ActionStatus.FAILURE;
		} else {
			PickupModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.PICKUP_MODIFIER);
			Set<PickupModifier> pickupModifiers =
				modifierFactory.findModifiers(new PickupContext(game, player, selectedModifierSkills));

			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC)
				.forName(Mechanic.Type.AGILITY.name());
			int minimumRoll;
			if (secureTheBall) {
				minimumRoll = mechanic.minimumRoll(2, pickupModifiers);
			} else {
				minimumRoll = mechanic.minimumRollPickup(player, pickupModifiers);
			}
			boolean doRoll = pickupRoll == 0;
			if (doRoll) {
				pickupRoll = getGameState().getDiceRoller().rollSkill();
			}
			boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(pickupRoll, minimumRoll);
			if (doRoll) {
				getResult().addReport(new ReportPickupRoll(player.getId(), successful, pickupRoll,
					minimumRoll, reRollUsed, pickupModifiers.toArray(new PickupModifier[0]), secureTheBall));
			}
			if (successful) {
				return ActionStatus.SUCCESS;
			} else {
				return offerRescue(player, minimumRoll);
			}
		}
	}

	private ActionStatus offerRescue(Player<?> player, int minimumRoll) {
		Game game = getGameState().getGame();
		setReRolledAction(ReRolledActions.PICK_UP);
		// Secure the Ball is not an Agility Test and retains its existing re-roll prompt.
		if (secureTheBall) {
			awaitingRescue = !reRollUsed && UtilServerReRoll.askForReRollIfAvailable(getGameState(), player,
				ReRolledActions.PICK_UP, minimumRoll, false);
		} else {
			List<ModifierChoiceOption> options = selectionService.findOptions(game, player, pickupRoll);
			List<ModifierChoiceOption> combinations = selectionService.findCombinations(game, player);
			ReRollSource skillReRoll = reRollUsed ? null : (player == game.getActingPlayer().getPlayer()
				? UtilCards.getUnusedRerollSource(game.getActingPlayer(), ReRolledActions.PICK_UP)
				: UtilCards.getRerollSource(player, ReRolledActions.PICK_UP));
			if (options.isEmpty() && skillReRoll != null
				&& !stallingExtension.wouldEndOfTurnTriggerStallingRoll(game, player)) {
				setReRollSource(skillReRoll);
				if (UtilServerReRoll.useReRoll(this, skillReRoll, player)) {
					reRollUsed = true;
					pickupRoll = 0;
					return pickUp(player);
				}
				return ActionStatus.FAILURE;
			}
			awaitingRescue = getGameState().getReRollService().askForReRollIfAvailable(
				ReRollRequest.forPlayer(getGameState(), player, ReRolledActions.PICK_UP, minimumRoll)
					.reRollSkill(skillReRoll == null ? null : skillReRoll.getSkill(game))
					.dialogParameter(new ReRollModifierChoiceDialogParameterFactory(
						pickupRoll, options, combinations, !reRollUsed))
					.build());
		}
		return awaitingRescue ? ActionStatus.WAITING_FOR_RE_ROLL : ActionStatus.FAILURE;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, thrownPlayerId);
		IServerJsonOption.IGNORE.addTo(jsonObject, ignore);
		IServerJsonOption.SECURE_THE_BALL_USED.addTo(jsonObject, secureTheBall);
		IServerJsonOption.PICK_UP_OPTIONAL.addTo(jsonObject, optionalPickUp);
		IServerJsonOption.ATTEMPT_PICK_UP.addTo(jsonObject, attemptPickUp);
		IServerJsonOption.PLAYER_ON_BALL_ID.addTo(jsonObject, overridePlayerId);
		IServerJsonOption.PICKUP_ROLL.addTo(jsonObject, pickupRoll);
		IServerJsonOption.RE_ROLL_USED.addTo(jsonObject, reRollUsed);
		IServerJsonOption.AWAITING_RESCUE.addTo(jsonObject, awaitingRescue);
		JsonArray skills = new JsonArray();
		selectedModifierSkills.forEach(skill -> skills.add(skill.getName()));
		IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.addTo(jsonObject, skills);
		return jsonObject;
	}

	@Override
	public StepPickUp initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		thrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		ignore = toPrimitive(IServerJsonOption.IGNORE.getFrom(source, jsonObject));
		secureTheBall = toPrimitive(IServerJsonOption.SECURE_THE_BALL_USED.getFrom(source, jsonObject));
		optionalPickUp = toPrimitive(IServerJsonOption.PICK_UP_OPTIONAL.getFrom(source, jsonObject));
		attemptPickUp = IServerJsonOption.ATTEMPT_PICK_UP.getFrom(source, jsonObject);
		overridePlayerId = IServerJsonOption.PLAYER_ON_BALL_ID.getFrom(source, jsonObject);
		pickupRoll = IServerJsonOption.PICKUP_ROLL.isDefinedIn(jsonObject)
			? IServerJsonOption.PICKUP_ROLL.getFrom(source, jsonObject) : 0;
		reRollUsed = toPrimitive(IServerJsonOption.RE_ROLL_USED.getFrom(source, jsonObject));
		awaitingRescue = IServerJsonOption.AWAITING_RESCUE.isDefinedIn(jsonObject)
			? toPrimitive(IServerJsonOption.AWAITING_RESCUE.getFrom(source, jsonObject))
			: getReRolledAction() == ReRolledActions.PICK_UP;
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
