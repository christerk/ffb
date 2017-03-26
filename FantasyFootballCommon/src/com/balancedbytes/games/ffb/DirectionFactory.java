package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.util.ArrayTool;

/**
 * 
 * @author Kalimar
 */
public class DirectionFactory implements INamedObjectFactory {
  
  public Direction forName(String pName) {
    for (Direction direction : Direction.values()) {
      if (direction.getName().equalsIgnoreCase(pName)) {
        return direction;
      }
    }
    return null;
  }

  public Direction forRoll(int pRoll) {
    switch (pRoll) {
      case 1:
        return Direction.NORTH;
      case 2:
        return Direction.NORTHEAST;
      case 3:
        return Direction.EAST;
      case 4:
        return Direction.SOUTHEAST;
      case 5:
        return Direction.SOUTH;
      case 6:
        return Direction.SOUTHWEST;
      case 7:
        return Direction.WEST;
      case 8:
        return Direction.NORTHWEST;
      default:
        return null;
    }
  }

  public Direction transform(Direction pDirection) {
    if (pDirection == null) {
      return null;
    }
    switch (pDirection) {
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
        return pDirection;
    }
  }
  
  public Direction[] transform(Direction[] pDirections) {
    Direction[] transformedDirections = new Direction[0];
    if (ArrayTool.isProvided(pDirections)) {
      transformedDirections = new Direction[pDirections.length];
      for (int i = 0; i < transformedDirections.length; i++) {
        transformedDirections[i] = transform(pDirections[i]);
      }
    }
    return transformedDirections;
  }

}
