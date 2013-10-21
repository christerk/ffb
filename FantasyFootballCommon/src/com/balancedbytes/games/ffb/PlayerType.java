package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum PlayerType implements IEnumWithId, IEnumWithName {
  
  REGULAR(1, "Regular"),
  BIG_GUY(2, "Big Guy"),
  STAR(3, "Star"),
  IRREGULAR(4, "Irregular"),
  JOURNEYMAN(5, "Journeyman"),
  RAISED_FROM_DEAD(6, "RaisedFromDead"),
  MERCENARY(7, "Mercenary");
  
  private int fId;
  private String fName;
  
  private PlayerType(int pId, String pName) {
    fId = pId;
    fName = pName;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
}
