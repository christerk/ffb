package com.balancedbytes.games.ffb.server.step.action.pass;

import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
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
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in the pass sequence to handle skill ANIMOSITY.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Expects stepParameter CATCHER_ID to be set by a preceding step.
 * 
 * @author Kalimar
 */
public final class StepAnimosity extends AbstractStepWithReRoll {

  public class StepState {
    public String catcherId;
    public String gotoLabelOnFailure;
    
    // Transients
    public boolean doRoll;
  }

  public StepState state;

  public StepAnimosity(GameState pGameState) {
    super(pGameState);
    state = new StepState();
  }

  public StepId getId() {
    return StepId.ANIMOSITY;
  }

  @Override
  public void init(StepParameterSet pParameterSet) {
    if (pParameterSet != null) {
      for (StepParameter parameter : pParameterSet.values()) {
        switch (parameter.getKey()) {
        // mandatory
        case GOTO_LABEL_ON_FAILURE:
          state.gotoLabelOnFailure = (String) parameter.getValue();
          break;
        default:
          break;
        }
      }
    }
    if (state.gotoLabelOnFailure == null) {
      throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
    }
  }

  @Override
  public boolean setParameter(StepParameter pParameter) {
    if ((pParameter != null) && !super.setParameter(pParameter)) {
      switch (pParameter.getKey()) {
      case CATCHER_ID:
        state.catcherId = (String) pParameter.getValue();
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
    if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
      executeStep();
    }
    return commandStatus;
  }

  private void executeStep() {
    state.doRoll = false;
    Game game = getGameState().getGame();
    if (game.getTurnMode().isBombTurn()) {
      getResult().setNextAction(StepAction.NEXT_STEP);
      return;
    }
    
    getGameState().executeStepHooks(this, state);
    
    if (!state.doRoll) {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, state.gotoLabelOnFailure);
    IServerJsonOption.CATCHER_ID.addTo(jsonObject, state.catcherId);
    return jsonObject;
  }

  @Override
  public StepAnimosity initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    state.gotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(jsonObject);
    state.catcherId = IServerJsonOption.CATCHER_ID.getFrom(jsonObject);
    return this;
  }

}
