package com.fumbbl.ffb.server.step;

/**
 * 
 * @author Kalimar
 */
public enum StepCommandStatus {

	UNHANDLED_COMMAND(1), EXECUTE_STEP(2), SKIP_STEP(3);

	private final int fId;

	StepCommandStatus(int pId) {
		fId = pId;
	}

	public int getId() {
		return fId;
	}

}
