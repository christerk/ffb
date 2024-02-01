package com.fumbbl.ffb;

public enum LeaderState implements INamedObject {

	NONE("none"), AVAILABLE("available"), USED("used");

	private String fName;

	private LeaderState(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
