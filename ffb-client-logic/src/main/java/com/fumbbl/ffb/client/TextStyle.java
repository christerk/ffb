package com.fumbbl.ffb.client;

/**
 * @author Dominic Schabel
 */
public enum TextStyle {

	NONE(""), BOLD("bold"), HOME("home"), HOME_BOLD("homeBold"), AWAY("away"), AWAY_BOLD("awayBold"),
	SPECTATOR("spectator"), ADMIN("admin"), DEV("dev"), ROLL("roll"), NEEDED_ROLL("neededRoll"),
	EXPLANATION("explanation"), TURN("turn"), TURN_HOME("turnHome"), TURN_AWAY("turnAway");

	private final String fName;

	TextStyle(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}
}
