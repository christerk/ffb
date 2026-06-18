package com.fumbbl.ffb;

/**
 * @author Kalimar
 */
public enum ClientMode implements INamedObject {

    NO_COACH_NO_CONNECTION("noCoachNoConnection", "-noCoachNoConnection"),
    PLAYER("player", "-player"),
    SPECTATOR("spectator", "-spectator"),
    REPLAY("replay", "-replay");

	private final String fName;
	private final String fArgument;

	ClientMode(String pName, String pArgument) {
		fName = pName;
		fArgument = pArgument;
	}

	public String getName() {
		return fName;
	}

	public String getArgument() {
		return fArgument;
	}

}
