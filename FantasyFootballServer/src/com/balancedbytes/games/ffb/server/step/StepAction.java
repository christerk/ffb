package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.IEnumWithId;
import com.balancedbytes.games.ffb.IEnumWithName;

/**
 * 
 * @author Kalimar
 */
public enum StepAction implements IEnumWithId, IEnumWithName {

	CONTINUE(1, "continue"),
	NEXT_STEP(2, "nextStep"),
	GOTO_LABEL(3, "gotoLabel"),
	NEXT_STEP_AND_REPEAT(4, "nextStepAndRepeat"),
	GOTO_LABEL_AND_REPEAT(5, "gotoLabelAndRepeat");
	
	private int fId;
	private String fName;
	
	private StepAction(int pId, String pName) {
		fId = pId;
		fName = pName;
	}
	
	public int getId() {
		return fId;
	}
	
	public String getName() {
	  return fName;
	}
	
}
