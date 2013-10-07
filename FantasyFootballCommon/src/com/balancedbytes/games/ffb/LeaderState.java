package com.balancedbytes.games.ffb;

public enum LeaderState implements IEnumWithId, IEnumWithName {
  
  NONE(1, "none"),
  AVAILABLE(2, "available"),
  USED(3, "used");
  
  private int fId;
  private String fName;
  
  private LeaderState(int pValue, String pName) {
    fId = pValue;
    fName = pName;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
}
