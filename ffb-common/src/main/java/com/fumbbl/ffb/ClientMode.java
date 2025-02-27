package com.fumbbl.ffb;

import java.util.ArrayList;

/**
 * @author Kalimar
 */
public enum ClientMode implements INamedObject {

	PLAYER("player", "-player"), SPECTATOR("spectator", "-spectator"),
	REPLAY("replay", "-replay"), SHARED_REPLAY("sharedReplay", "-sharedReplay");

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

	public boolean isReplay() {
		return new ArrayList<ClientMode>() {{
			add(REPLAY);
			add(SHARED_REPLAY);
		}}.contains(this);
	}
}
