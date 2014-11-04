package com.balancedbytes.games.ffb.server.step.action.common;

import java.util.Set;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PickupModifier;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
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
  			switch (parameter.getKey()) {
  				case GOTO_LABEL_ON_FAILURE:
  					fGotoLabelOnFailure = (String) parameter.getValue();
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
      if (ReRolledAction.PICK_UP == getReRolledAction()) {
        if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
        	doPickUp = false;
          publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
        	publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.FAILED_PICK_UP));
        	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
        }
      }
      if (doPickUp) {
        switch (pickUp()) {
	        case SUCCESS:
	          game.getFieldModel().setBallMoving(false);
	          getResult().setSound(Sound.PICKUP);
	        	getResult().setNextAction(StepAction.NEXT_STEP);
	          break;
	        case FAILURE:
	          publishParameter(new StepParameter(StepParameterKey.FEEDING_ALLOWED, false));
	          publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
	          publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.FAILED_PICK_UP));
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
    return (game.getFieldModel().isBallInPlay() && game.getFieldModel().isBallMoving() && playerCoordinate.equals(game.getFieldModel().getBallCoordinate()));
  }
  
  private ActionStatus pickUp() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (UtilCards.hasSkill(game, actingPlayer, Skill.NO_HANDS)) {
      return ActionStatus.FAILURE;
    } else {
      Set<PickupModifier> pickupModifiers = PickupModifier.findPickupModifiers(game);
      int minimumRoll = DiceInterpreter.getInstance().minimumRollPickup(actingPlayer.getPlayer(), pickupModifiers);
      int roll = getGameState().getDiceRoller().rollSkill();
      boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
      PickupModifier[] pickupModifierArray = PickupModifier.toArray(pickupModifiers);
      boolean reRolled = ((getReRolledAction() == ReRolledAction.PICK_UP) && (getReRollSource() != null));
      getResult().addReport(new ReportSkillRoll(ReportId.PICK_UP_ROLL, actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, pickupModifierArray));
      if (successful) {
        return ActionStatus.SUCCESS;
      } else {
        if (getReRolledAction() != ReRolledAction.PICK_UP) {
          setReRolledAction(ReRolledAction.PICK_UP);
          if (UtilCards.hasUnusedSkill(game, actingPlayer, Skill.SURE_HANDS)) {
            setReRollSource(ReRollSource.SURE_HANDS);
            UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer());
            return pickUp();
          } else {
            if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.PICK_UP, minimumRoll, false)) {
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
  
  // ByteArray serialization
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnFailure = pByteArray.getString();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    return jsonObject;
  }
  
  @Override
  public StepPickUp initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(jsonObject);
    return this;
  }

}
