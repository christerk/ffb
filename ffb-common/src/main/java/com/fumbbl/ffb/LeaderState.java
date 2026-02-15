package com.fumbbl.ffb;

public enum LeaderState implements INamedObject {

	NONE("none"), AVAILABLE("available"), USED("used");

	private final String fName;

	LeaderState(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
