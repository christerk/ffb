package com.fumbbl.ffb;

/**
 * 
 * @author Kalimar
 */
public enum PushbackMode implements INamedObject {

	REGULAR("regular"), SIDE_STEP("sideStep"), GRAB("grab");

	private String fName;

	private PushbackMode(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
