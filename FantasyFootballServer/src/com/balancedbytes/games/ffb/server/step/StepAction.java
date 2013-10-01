package com.balancedbytes.games.ffb.server.step;

/**
 * 
 * @author Kalimar
 */
public enum StepAction {

	CONTINUE(1),
	NEXT_STEP(2),
	GOTO_LABEL(3),
	NEXT_STEP_AND_REPEAT_COMMAND(4),
	GOTO_LABEL_AND_REPEAT_COMMAND(5);
	
	private int fId;
	
	private StepAction(int pId) {
		fId = pId;
	}
	
	public int getId() {
		return fId;
	}
	
	public static StepAction fromId(int pId) {
		for (StepAction stepAction : values()) {
			if (stepAction.getId() == pId) {
				return stepAction;
			}
		}
		return null;
	}

}
