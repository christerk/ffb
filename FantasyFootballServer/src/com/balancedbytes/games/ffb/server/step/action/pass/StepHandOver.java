package com.balancedbytes.games.ffb.server.step.action.pass;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.report.ReportHandOver;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in the pass sequence to handle a hand over of the ball.
 * 
 * Expects stepParameter CATCHER_ID to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepHandOver extends AbstractStepWithReRoll {
	
  private String fCatcherId;
	
	public StepHandOver(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.HAND_OVER;
	}
	
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case CATCHER_ID:
					fCatcherId = (String) pParameter.getValue();
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
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

  private void executeStep() {
    Game game = getGameState().getGame();
    game.getFieldModel().setBallMoving(true);
    game.setPassCoordinate(null);
    Player catcher = game.getPlayerById(fCatcherId);
    FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(catcher);
    game.getFieldModel().setBallCoordinate(catcherCoordinate);
    getResult().addReport(new ReportHandOver(fCatcherId));
    publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_HAND_OFF));
    publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
    getResult().setNextAction(StepAction.NEXT_STEP);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
    super.addTo(pByteList);
    pByteList.addString(fCatcherId);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = super.initFrom(pByteArray);
    fCatcherId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
    return jsonObject;
  }
  
  @Override
  public StepHandOver initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(jsonObject);
    return this;
  }
  
}
