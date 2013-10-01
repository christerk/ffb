package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.server.GameState;

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

  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
}
