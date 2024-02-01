package com.fumbbl.ffb.server.step;

import com.fumbbl.ffb.INamedObject;

/**
 * 
 * @author Kalimar
 */
public enum StepAction implements INamedObject {

	CONTINUE("continue", false, false, false, false),
	NEXT_STEP("nextStep", true, false, false, false),
	REPEAT("repeat", false, false, false, true),
	GOTO_LABEL("gotoLabel", true, false, true, false),
	NEXT_STEP_AND_REPEAT("nextStepAndRepeat", true, true, false, false),
	GOTO_LABEL_AND_REPEAT("gotoLabelAndRepeat", true, true, true, false);

	private final String fName;
	private final boolean fTriggerNextStep;
	private final boolean fForwardCommand;
	private final boolean fTriggerGoto;
	private final boolean fTriggerRepeat;

	StepAction(String pName, boolean triggerNextStep, boolean forwardCommand, boolean triggerGoto,
						 boolean triggerRepeat) {
		fName = pName;
		fTriggerNextStep = triggerNextStep;
		fForwardCommand = forwardCommand;
		fTriggerGoto = triggerGoto;
		fTriggerRepeat = triggerRepeat;
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

	public boolean triggerRepeat() {
		return fTriggerRepeat;
	}
}
