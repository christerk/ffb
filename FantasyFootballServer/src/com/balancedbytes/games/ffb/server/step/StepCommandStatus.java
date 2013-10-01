package com.balancedbytes.games.ffb.server.step;

/**
 * 
 * @author Kalimar
 */
public enum StepCommandStatus {

	UNHANDLED_COMMAND(1),
	EXECUTE_STEP(2),
	SKIP_STEP(3);
	
	private int fId;
	
	private StepCommandStatus(int pId) {
		fId = pId;
	}
	
	public int getId() {
		return fId;
	}
	
	public static StepCommandStatus fromId(int pId) {
		for (StepCommandStatus stepAction : values()) {
			if (stepAction.getId() == pId) {
				return stepAction;
			}
		}
		return null;
	}

}
