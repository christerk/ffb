package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.PickupModifierFactory;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.mechanics.AgilityMechanic;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.PickupContext;
import com.balancedbytes.games.ffb.modifiers.PickupModifier;
import com.balancedbytes.games.ffb.report.ReportPickupRoll;
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
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Set;

/**
 * Step in block sequence to handle picking up the ball.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepPickUp extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure;

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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		boolean doPickUp = true;
		if (isPickUp()) {
			if (ReRolledActions.PICK_UP == getReRolledAction()) {
				if ((getReRollSource() == null)
						|| !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
					doPickUp = false;
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
					publishParameter(
							new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.FAILED_PICK_UP));
					getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
				}
			}
			if (doPickUp) {
				switch (pickUp()) {
				case SUCCESS:
					game.getFieldModel().setBallMoving(false);
					getResult().setSound(SoundId.PICKUP);
					getResult().setNextAction(StepAction.NEXT_STEP);
					break;
				case FAILURE:
					publishParameter(new StepParameter(StepParameterKey.FEEDING_ALLOWED, false));
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
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

	private boolean isPickUp() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		return (game.getFieldModel().isBallInPlay() && game.getFieldModel().isBallMoving()
				&& playerCoordinate.equals(game.getFieldModel().getBallCoordinate()));
	}

	private ActionStatus pickUp() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.preventHoldBall)) {
			return ActionStatus.FAILURE;
		} else {
			PickupModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.PICKUP_MODIFIER);
			Set<PickupModifier> pickupModifiers = modifierFactory.findModifiers(new PickupContext(game, actingPlayer.getPlayer()));
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
			int minimumRoll = mechanic.minimumRollPickup(actingPlayer.getPlayer(), pickupModifiers);
			int roll = getGameState().getDiceRoller().rollSkill();
			boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
			boolean reRolled = ((getReRolledAction() == ReRolledActions.PICK_UP) && (getReRollSource() != null));
			getResult().addReport(new ReportPickupRoll(actingPlayer.getPlayerId(), successful, roll,
					minimumRoll, reRolled, pickupModifiers.toArray(new PickupModifier[0])));
			if (successful) {
				return ActionStatus.SUCCESS;
			} else {
				if (getReRolledAction() != ReRolledActions.PICK_UP) {
					setReRolledAction(ReRolledActions.PICK_UP);
					ReRollSource unusedPickupReroll = UtilCards.getUnusedRerollSource(actingPlayer, ReRolledActions.PICK_UP);
					if (unusedPickupReroll != null) {
						setReRollSource(unusedPickupReroll);
						UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer());
						return pickUp();
					} else {
						if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(),
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
		return jsonObject;
	}

	@Override
	public StepPickUp initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		return this;
	}

}
