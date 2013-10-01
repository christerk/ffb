package com.balancedbytes.games.ffb.server;

/**
 * 
 * @author Kalimar
 */
public enum GameCacheMode {
	
	LOAD_GAME("LOAD GAME"),
	REPLAY_GAME("REPLAY GAME"),
	START_GAME("START GAME"),
	START_TEST_GAME("START TEST GAME"),
	SCHEDULE_GAME("SCHEDULE GAME"),
	ADMIN_GAME("ADMIN GAME");
	
	private String fName;
	
	private GameCacheMode(String pName) {
		fName = pName;
	}
	
	public String getName() {
	  return fName;
  }

}
