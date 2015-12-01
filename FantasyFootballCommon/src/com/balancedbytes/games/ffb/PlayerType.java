package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum PlayerType implements IEnumWithName {
  
  REGULAR("Regular"),
  BIG_GUY("Big Guy"),
  STAR("Star"),
  IRREGULAR("Irregular"),
  JOURNEYMAN("Journeyman"),
  RAISED_FROM_DEAD("RaisedFromDead"),
  MERCENARY("Mercenary");
  
  private String fName;
  
  private PlayerType(String pName) {
    fName = pName;
  }
  
  public String getName() {
    return fName;
  }
  
}
