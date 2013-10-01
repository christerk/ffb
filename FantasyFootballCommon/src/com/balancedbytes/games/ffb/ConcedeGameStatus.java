package com.balancedbytes.games.ffb;



/**
 * 
 * @author Kalimar
 */
public enum ConcedeGameStatus {
  
  REQUESTED(1, "requested"),
  CONFIRMED(2, "confirmed"),
  DENIED(3, "denied");
  
  private int fId;
  private String fName;
  
  private ConcedeGameStatus(int pValue, String pName) {
    fId = pValue;
    fName = pName;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public static ConcedeGameStatus fromId(int pId) {
    for (ConcedeGameStatus mode : values()) {
      if (mode.getId() == pId) {
        return mode;
      }
    }
    return null;
  }
  
  public static ConcedeGameStatus fromName(String pName) {
    for (ConcedeGameStatus mode : values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }
    
}
