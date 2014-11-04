package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.server.GameState;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in any sequence to goto next step.
 * 
 * @author Kalimar
 */
public class StepNextStep extends AbstractStep {
	
	public StepNextStep(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.NEXT_STEP;
	}
	
	@Override
	public void start() {
		super.start();
		getResult().setNextAction(StepAction.NEXT_STEP);
	}
	
	// ByteArray serialization

  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    return super.toJsonValue();
  }
  
  @Override
  public StepNextStep initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    return this;
  }
  
}
