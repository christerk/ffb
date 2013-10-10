package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public enum PushbackMode implements IEnumWithId, IEnumWithName {
  
  REGULAR(1, "regular"),
  SIDE_STEP(2, "sideStep"),
  GRAB(3, "grab");
  
  private int fId;
  private String fName;
  
  private PushbackMode(int pId, String pName) {
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
