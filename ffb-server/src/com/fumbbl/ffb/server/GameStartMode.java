package com.fumbbl.ffb.server;

/**
 * 
 * @author Kalimar
 */
public enum GameStartMode {

	START_GAME("START GAME"), START_TEST_GAME("START TEST GAME"), SCHEDULE_GAME("SCHEDULE GAME");

	private String fName;

	private GameStartMode(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
