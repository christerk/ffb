package com.fumbbl.ffb;

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

	public Direction transform() {
		switch (this) {
		case NORTHEAST:
			return Direction.NORTHWEST;
		case EAST:
			return Direction.WEST;
		case SOUTHEAST:
			return Direction.SOUTHWEST;
		case SOUTHWEST:
			return Direction.SOUTHEAST;
		case WEST:
			return Direction.EAST;
		case NORTHWEST:
			return Direction.NORTHEAST;
		default:
			return this;
		}
	}	
}
