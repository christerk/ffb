package com.fumbbl.ffb;

/**
 * @author Kalimar
 */
public enum PlayerType implements INamedObject {

	REGULAR("Regular"), BIG_GUY("Big Guy"), STAR("Star"), IRREGULAR("Irregular"),
	RIOTOUS_ROOKIE("RiotousRookie"), RAISED_FROM_DEAD("RaisedFromDead"), MERCENARY("Mercenary"),
	PLAGUE_RIDDEN("PlagueRidden"), INFAMOUS_STAFF("Infamous Staff");

	private final String fName;

	PlayerType(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
