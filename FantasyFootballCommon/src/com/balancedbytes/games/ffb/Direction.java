package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public enum Direction implements INamedObject {

	NORTH("North"), NORTHEAST("Northeast"), EAST("East"), SOUTHEAST("Southeast"), SOUTH("South"), SOUTHWEST("Southwest"),
	WEST("West"), NORTHWEST("Northwest");

	private String fName;

	private Direction(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
