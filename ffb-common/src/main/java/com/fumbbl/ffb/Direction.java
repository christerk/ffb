package com.fumbbl.ffb;

import java.util.Arrays;

/**
 * @author Kalimar
 */
public enum Direction implements INamedObject {

	NORTH("North"), NORTHEAST("Northeast"), EAST("East"), SOUTHEAST("Southeast"), SOUTH("South"), SOUTHWEST("Southwest"),
	WEST("West"), NORTHWEST("Northwest");

	private final String fName;

	Direction(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

	public static Direction forName(String name) {
		return Arrays.stream(values()).filter(direction -> direction.fName.equalsIgnoreCase(name)).findFirst().orElse(null);
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
