package com.fumbbl.ffb.mechanics.bb2025;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ThrowInMechanic extends com.fumbbl.ffb.mechanics.ThrowInMechanic {
  
  @Override
	public int distance(int[] distanceRoll) {
		return distanceRoll[0] + distanceRoll[1];
	}

  @Override
  public boolean isCornerThrowIn(FieldCoordinate pStartCoordinate) {
    return (pStartCoordinate.getX() < 1 || pStartCoordinate.getX() > 24)
      && (pStartCoordinate.getY() < 1 || pStartCoordinate.getY() > 13);
  }

  @Override
  public Direction interpretThrowInDirectionRoll(FieldCoordinate pStartCoordinate, int roll,
      boolean isCornerThrowIn) {
    if (isCornerThrowIn) {
      if (pStartCoordinate.getX() < 1 && pStartCoordinate.getY() < 1) {
        return interpretCornerThrowInDirectionRoll(Direction.NORTHWEST, roll);
      }
      if (pStartCoordinate.getX() > 24 && pStartCoordinate.getY() < 1) {
        return interpretCornerThrowInDirectionRoll(Direction.NORTHEAST, roll);
      }
      if (pStartCoordinate.getX() < 1 && pStartCoordinate.getY() > 13) {
        return interpretCornerThrowInDirectionRoll(Direction.SOUTHWEST, roll);
      }
      if (pStartCoordinate.getX() > 24 && pStartCoordinate.getY() > 13) {
        return interpretCornerThrowInDirectionRoll(Direction.SOUTHEAST, roll);
      }
    } else {
      // Endzone Home Team
      if (pStartCoordinate.getX() < 1) {
        return interpretThrowInDirectionRoll(Direction.EAST, roll);
      }
      // Endzone Away Team
      if (pStartCoordinate.getX() > 24) {
        return interpretThrowInDirectionRoll(Direction.WEST, roll);
      }
      // Lower Sideline
      if (pStartCoordinate.getY() > 13) {
        return interpretThrowInDirectionRoll(Direction.NORTH, roll);
      }
      // Upper Sideline
      if (pStartCoordinate.getY() < 1) {
        return interpretThrowInDirectionRoll(Direction.SOUTH, roll);
      }
    }

		throw new IllegalStateException("Unable to determine throwInDirection.");
  }

  private Direction interpretCornerThrowInDirectionRoll(Direction pCornerDirection, int roll) {
    if (pCornerDirection == Direction.NORTHWEST) {
      switch (roll) {
      case 1:
        return Direction.EAST;
      case 2:
        return Direction.SOUTHEAST;
      case 3:
        return Direction.SOUTH;
      }
    }
    if (pCornerDirection == Direction.NORTHEAST) {
      switch (roll) {
      case 1:
        return Direction.SOUTH;
      case 2:
        return Direction.SOUTHWEST;
      case 3:
        return Direction.WEST;
      }
    }
    if (pCornerDirection == Direction.SOUTHWEST) {
      switch (roll) {
      case 1:
        return Direction.NORTH;
      case 2:
        return Direction.NORTHEAST;
      case 3:
        return Direction.EAST;
      }
    }
    if (pCornerDirection == Direction.SOUTHEAST) {
      switch (roll) {
      case 1:
        return Direction.WEST;
      case 2:
        return Direction.NORTHWEST;
      case 3:
        return Direction.NORTH;
      }
    }
    throw new IllegalStateException("Unable to determine cornerThrowInDirection.");
  }
  
}
