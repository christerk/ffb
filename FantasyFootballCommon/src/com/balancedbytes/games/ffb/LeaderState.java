package com.balancedbytes.games.ffb;

public enum LeaderState {
  
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
  
  public static LeaderState fromId(int pId) {
    for (LeaderState state : values()) {
      if (state.getId() == pId) {
        return state;
      }
    }
    return null;
  }
    
  public static LeaderState fromName(String pName) {
    for (LeaderState state : values()) {
      if (state.getName().equalsIgnoreCase(pName)) {
        return state;
      }
    }
    return null;
  }
  
}
