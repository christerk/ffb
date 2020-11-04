package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill HORNS.
 * 
 * @author Kalimar
 */
public class StepHorns extends AbstractStep {
	
	public class StepState {
		public ActionStatus status;
		public Boolean usingHorns;
	  }
	
	private StepState state;
	
	public StepHorns(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}
	
	public StepId getId() {
		return StepId.HORNS;
	}
	
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
  private void executeStep() {
	  getGameState().executeStepHooks(this, state);
  }
    
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.USING_HORNS.addTo(jsonObject, state.usingHorns);
    return jsonObject;
  }
  
  @Override
  public StepHorns initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    state.usingHorns = IServerJsonOption.USING_HORNS.getFrom(jsonObject);
    return this;
  }

}
