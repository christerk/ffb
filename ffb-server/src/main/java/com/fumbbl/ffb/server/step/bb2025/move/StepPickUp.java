package com.fumbbl.ffb.server.step.bb2025.move;

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
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.PickupModifierFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.PickupContext;
import com.fumbbl.ffb.modifiers.PickupModifier;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPickUpChoice;
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
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

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
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && pReceivedCommand.getId() == NetCommandId.CLIENT_PICK_UP_CHOICE) {
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
		Player<?> player = StringTool.isProvided(overridePlayerId)
			? game.getPlayerById(overridePlayerId)
			: (StringTool.isProvided(thrownPlayerId) ? game.getPlayerById(thrownPlayerId) : game.getActingPlayer().getPlayer());
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
				if (ReRolledActions.PICK_UP == getReRolledAction()) {
					if ((getReRollSource() == null)
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
								new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.FAILED_PICK_UP));
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
				publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.FAILED_PICK_UP));
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
			}
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private boolean isPickUp(Player<?> player) {
		Game game = getGameState().getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		return (
			!ignore
				&& game.getFieldModel().isBallInPlay()
				&& game.getFieldModel().isBallMoving()
				&& playerCoordinate.equals(game.getFieldModel().getBallCoordinate())
		);
	}

	private ActionStatus pickUp(Player<?> player) {
		Game game = getGameState().getGame();
		if (player.hasSkillProperty(NamedProperties.preventHoldBall) || player.hasSkillProperty(NamedProperties.preventPickup)) {
			return ActionStatus.FAILURE;
		} else {
			PickupModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.PICKUP_MODIFIER);
			Set<PickupModifier> pickupModifiers = modifierFactory.findModifiers(new PickupContext(game, player));

			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
			int minimumRoll;
			if (secureTheBall) {
				minimumRoll = mechanic.minimumRoll(2, pickupModifiers);
			} else {
				minimumRoll = mechanic.minimumRollPickup(player, pickupModifiers);
			}
			int roll = getGameState().getDiceRoller().rollSkill();
			boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
			boolean reRolled = ((getReRolledAction() == ReRolledActions.PICK_UP) && (getReRollSource() != null));
			getResult().addReport(new ReportPickupRoll(player.getId(), successful, roll,
				minimumRoll, reRolled, pickupModifiers.toArray(new PickupModifier[0])));
			if (successful) {
				return ActionStatus.SUCCESS;
			} else {
				if (getReRolledAction() != ReRolledActions.PICK_UP) {
					setReRolledAction(ReRolledActions.PICK_UP);
					ReRollSource rerollSource = UtilCards.getRerollSource(player, ReRolledActions.PICK_UP);
					if (rerollSource != null && !secureTheBall) {
						setReRollSource(rerollSource);
						UtilServerReRoll.useReRoll(this, getReRollSource(), player);
						return pickUp(player);
					} else {
						if (!reRolled && UtilServerReRoll.askForReRollIfAvailable(getGameState(), player,
							ReRolledActions.PICK_UP, minimumRoll, false)) {
							return ActionStatus.WAITING_FOR_RE_ROLL;
						} else {
							return ActionStatus.FAILURE;
						}
					}
				} else {
					return ActionStatus.FAILURE;
				}
			}
		}
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
		return this;
	}

}
