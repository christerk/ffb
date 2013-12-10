package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step to end kickoff sequence.
 * 
 * Expects stepParameter KICKING_PLAYER_COORDINATE to be set by a preceding step.
 * Expects stepParameter TOUCHBACK to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepKickoffAnimation extends AbstractStep {
	
  private FieldCoordinate fKickingPlayerCoordinate;
  private boolean fTouchback;

	public StepKickoffAnimation(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.KICKOFF_ANIMATION;
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
	
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case KICKING_PLAYER_COORDINATE:
					fKickingPlayerCoordinate = (FieldCoordinate) pParameter.getValue();
					return true;
				case TOUCHBACK:
					fTouchback = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false; 
					return true;
				default:
					break;
			}
		}
		return false;
	}

  private void executeStep() {
    Game game = getGameState().getGame();
    if (fKickingPlayerCoordinate == null) {
      if (game.isHomePlaying()) {
      	fKickingPlayerCoordinate = new FieldCoordinate(2, 8);
      } else {
      	fKickingPlayerCoordinate = new FieldCoordinate(27, 8);
      }
    }
    game.getFieldModel().setBallInPlay(true);
    FieldCoordinate ballCoordinate = game.getFieldModel().getBallCoordinate();
    getResult().setAnimation(new Animation(AnimationType.KICK, fKickingPlayerCoordinate, ballCoordinate, null));
    if (!fTouchback) {
    	publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_KICKOFF));
    }
    getResult().setNextAction(StepAction.NEXT_STEP);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addFieldCoordinate(fKickingPlayerCoordinate);
  	pByteList.addBoolean(fTouchback);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fKickingPlayerCoordinate = pByteArray.getFieldCoordinate();
  	fTouchback = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.KICKING_PLAYER_COORDINATE.addTo(jsonObject, fKickingPlayerCoordinate);
    IServerJsonOption.TOUCHBACK.addTo(jsonObject, fTouchback);
    return jsonObject;
  }
  
  @Override
  public StepKickoffAnimation initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fKickingPlayerCoordinate = IServerJsonOption.KICKING_PLAYER_COORDINATE.getFrom(jsonObject);
    fTouchback = IServerJsonOption.TOUCHBACK.getFrom(jsonObject);
    return this;
  }

}
