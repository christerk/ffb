package com.balancedbytes.games.ffb;




/**
 * 
 * @author Kalimar
 */
public enum Direction implements IEnumWithId, IEnumWithName {
  
  NORTH(1, "North", 1),
  NORTHEAST(2, "Northeast", 8),
  EAST(3, "East", 7),
  SOUTHEAST(4, "Southeast", 6),
  SOUTH(5, "South", 5),
  SOUTHWEST(6, "Southwest", 4),
  WEST(7, "West", 3),
  NORTHWEST(8, "Northwest", 2);

  private int fId;
  private String fName;
  private int fTransformedValue;
  
  private Direction(int pId, String pName, int pTransformedValue) {
    fId = pId;
    fName = pName;
    fTransformedValue = pTransformedValue;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  protected int getTransformedValue() {
    return fTransformedValue;
  }

}
