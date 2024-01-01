package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
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
import com.fumbbl.ffb.report.ReportPickupRoll;
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
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepPickUp extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure, thrownPlayerId;

	private boolean ignore;

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
			if (parameter.getKey() == StepParameterKey.FOLLOWUP_CHOICE) {
				ignore = !toPrimitive((Boolean) parameter.getValue());
				return true;
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		Player<?> player = StringTool.isProvided(thrownPlayerId) ? game.getPlayerById(thrownPlayerId) : game.getActingPlayer().getPlayer();
		boolean doPickUp = true;
		if (player != null && isPickUp(player)) {
			if (ReRolledActions.PICK_UP == getReRolledAction()) {
				if ((getReRollSource() == null)
					|| !UtilServerReRoll.useReRoll(this, getReRollSource(), player)) {
					doPickUp = false;
					publishParameter(new StepParameter(StepParameterKey.FEEDING_ALLOWED, false));
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
					publishParameter(
						new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.FAILED_PICK_UP));
					getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
				}
			}
			if (doPickUp) {
				switch (pickUp(player)) {
					case SUCCESS:
						game.getFieldModel().setBallMoving(false);
						getResult().setSound(SoundId.PICKUP);
						getResult().setNextAction(StepAction.NEXT_STEP);
						break;
					case FAILURE:
						publishParameter(new StepParameter(StepParameterKey.FEEDING_ALLOWED, false));
						if (!player.hasSkillProperty(NamedProperties.preventPickup)) {
							publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
						}
						publishParameter(
							new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.FAILED_PICK_UP));
						getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
						break;
					default:
						break;
				}
			}
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private boolean isPickUp(Player<?> player) {
		Game game = getGameState().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		return (
			!ignore
				&& game.getFieldModel().isBallInPlay()
				&& game.getFieldModel().isBallMoving()
				&& playerCoordinate.equals(game.getFieldModel().getBallCoordinate())
				&& playerState.hasTacklezones()
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
			int minimumRoll = mechanic.minimumRollPickup(player, pickupModifiers);
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
					if (rerollSource != null) {
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
		return jsonObject;
	}

	@Override
	public StepPickUp initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		thrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		ignore = toPrimitive(IServerJsonOption.IGNORE.getFrom(source, jsonObject));
		return this;
	}

}
