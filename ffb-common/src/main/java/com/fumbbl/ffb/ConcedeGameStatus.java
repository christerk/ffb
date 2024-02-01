package com.fumbbl.ffb;

/**
 * 
 * @author Kalimar
 */
public enum ConcedeGameStatus implements INamedObject {

	REQUESTED("requested"), CONFIRMED("confirmed"), DENIED("denied");

	private String fName;

	private ConcedeGameStatus(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
