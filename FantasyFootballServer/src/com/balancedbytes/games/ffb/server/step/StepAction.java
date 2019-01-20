package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.INamedObject;

/**
 * 
 * @author Kalimar
 */
public enum StepAction implements INamedObject {

	CONTINUE("continue", false, false, false),
	NEXT_STEP("nextStep", true, false, false),
	GOTO_LABEL("gotoLabel", true, false, true),
	NEXT_STEP_AND_REPEAT("nextStepAndRepeat", true, true, false),
	GOTO_LABEL_AND_REPEAT("gotoLabelAndRepeat", true, true, true);
	
	private String fName;
	private boolean fTriggerNextStep;
	private boolean fForwardCommand;
	private boolean fTriggerGoto;
	
	private StepAction(String pName, boolean triggerNextStep, boolean forwardCommand, boolean triggerGoto) {
		fName = pName;
		fTriggerNextStep = triggerNextStep;
		fForwardCommand = forwardCommand;
		fTriggerGoto = triggerGoto;
	}
	
	public String getName() {
	  return fName;
	}

	public boolean triggerNextStep() {
		return fTriggerNextStep;
	}
	
	public boolean forwardCommand() {
		return fForwardCommand;
	}
	
	public boolean triggerGoto() {
		return fTriggerGoto;
	}
}
