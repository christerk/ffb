package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.util.ArrayTool;

/**
 * 
 * @author Kalimar
 */
public class DirectionFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public Direction forName(String pName) {
    for (Direction direction : Direction.values()) {
      if (direction.getName().equalsIgnoreCase(pName)) {
        return direction;
      }
    }
    return null;
  }
  
  public Direction forId(int pId) {
    for (Direction direction : Direction.values()) {
      if (direction.getId() == pId) {
        return direction;
      }
    }
    return null;
  }
  
  public Direction transform(Direction pDirection) {
    if (pDirection == null) {
      return null;
    }
    return forId(pDirection.getTransformedValue());
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
