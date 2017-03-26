package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.INamedObject;

/**
 * 
 * @author Kalimar
 */
public enum StepAction implements INamedObject {

	CONTINUE("continue"),
	NEXT_STEP("nextStep"),
	GOTO_LABEL("gotoLabel"),
	NEXT_STEP_AND_REPEAT("nextStepAndRepeat"),
	GOTO_LABEL_AND_REPEAT("gotoLabelAndRepeat");
	
	private String fName;
	
	private StepAction(String pName) {
		fName = pName;
	}
	
	public String getName() {
	  return fName;
	}
	
}
