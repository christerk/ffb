package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;


public abstract class ThrowInMechanic implements Mechanic {

	@Override
	public Type getType() {
		return Type.THROW_IN;
	}

	public abstract int distance(int[] distanceRoll);

	public abstract boolean isCornerThrowIn(FieldCoordinate startCoordinate);

	public abstract Direction interpretThrowInDirectionRoll(FieldCoordinate start, int roll);

	public Direction interpretThrowInDirectionRoll(Direction pTemplateDirection, int roll) {
		if (pTemplateDirection == Direction.EAST) {
			switch (roll) {
			case 1:
			case 2:
				return Direction.NORTHEAST;
			case 3:
			case 4:
				return Direction.EAST;
			case 5:
			case 6:
				return Direction.SOUTHEAST;
			}
		}
		if (pTemplateDirection == Direction.WEST) {
			switch (roll) {
			case 1:
			case 2:
				return Direction.SOUTHWEST;
			case 3:
			case 4:
				return Direction.WEST;
			case 5:
			case 6:
				return Direction.NORTHWEST;
			}
		}
		if (pTemplateDirection == Direction.NORTH) {
			switch (roll) {
			case 1:
			case 2:
				return Direction.NORTHWEST;
			case 3:
			case 4:
				return Direction.NORTH;
			case 5:
			case 6:
				return Direction.NORTHEAST;
			}
		}
		if (pTemplateDirection == Direction.SOUTH) {
			switch (roll) {
			case 1:
			case 2:
				return Direction.SOUTHEAST;
			case 3:
			case 4:
				return Direction.SOUTH;
			case 5:
			case 6:
				return Direction.SOUTHWEST;
			}
		}
		throw new IllegalStateException("Unable to determine throwInDirection.");
	}

}
