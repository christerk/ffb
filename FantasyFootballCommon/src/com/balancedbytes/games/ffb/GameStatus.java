package com.balancedbytes.games.ffb;




/**
 * 
 * @author Kalimar
 */
public enum GameStatus implements IEnumWithName {
  
  SCHEDULED("scheduled", "O"),
  STARTING("starting", "S"),
  ACTIVE("active", "A"),
  PAUSED("paused", "P"),
  FINISHED("finished", "F"),
  UPLOADED("uploaded", "U");
  
  private String fName;
  private String fTypeString;
  
  private GameStatus(String pName, String pTypeString) {
    fName = pName;
    fTypeString = pTypeString;
  }

  public String getName() {
    return fName;
  }
  
  public String getTypeString() {
    return fTypeString;
  }
    
}
