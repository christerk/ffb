/**
 * Copyright Sparkassen Informatik GmbH & Co. KG
 */
package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public enum PushbackMode {
  
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
  
  public static PushbackMode fromId(int pValue) {
    for (PushbackMode mode : values()) {
      if (mode.getId() == pValue) {
        return mode;
      }
    }
    return null;
  }
  
  public static PushbackMode fromName(String pName) {
    for (PushbackMode mode : values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

}
