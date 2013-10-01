package com.balancedbytes.games.ffb;




/**
 * 
 * @author Kalimar
 */
public enum GameStatus {
  
  SCHEDULED(1, "scheduled", "O"),
  STARTING(2, "starting", "S"),
  ACTIVE(3, "active", "A"),
  PAUSED(4, "paused", "P"),
  FINISHED(5, "finished", "F"),
  UPLOADED(6, "uploaded", "U");
  
  private int fId;
  private String fName;
  private String fTypeString;
  
  private GameStatus(int pValue, String pName, String pTypeString) {
    fId = pValue;
    fName = pName;
    fTypeString = pTypeString;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getTypeString() {
    return fTypeString;
  }
  
  public static GameStatus fromId(int pId) {
    for (GameStatus mode : values()) {
      if (mode.getId() == pId) {
        return mode;
      }
    }
    return null;
  }
  
  public static GameStatus fromName(String pName) {
    for (GameStatus mode : values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }
  
  public static GameStatus fromTypeString(String pTypeString) {
    for (GameStatus status : values()) {
      if (status.getTypeString().equals(pTypeString)) {
        return status;
      }
    }
    return null;
  }
    
}
