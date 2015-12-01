package com.balancedbytes.games.ffb;

public enum LeaderState implements IEnumWithName {
  
  NONE("none"),
  AVAILABLE("available"),
  USED("used");
  
  private String fName;
  
  private LeaderState(String pName) {
    fName = pName;
  }

  public String getName() {
    return fName;
  }
  
}
