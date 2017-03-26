package com.balancedbytes.games.ffb;




/**
 * 
 * @author Kalimar
 */
public enum GameStatus implements INamedObject {
  
  SCHEDULED("scheduled", "O"),
  STARTING("starting", "S"),
  ACTIVE("active", "A"),
  PAUSED("paused", "P"),
  FINISHED("finished", "F"),
  UPLOADED("uploaded", "U"),
  
  LOADING("loading", "L"),  // not written to db
  REPLAYING("replaying", "R");  // not written to db
  
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
