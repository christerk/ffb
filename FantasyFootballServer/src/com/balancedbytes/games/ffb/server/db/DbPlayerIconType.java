package com.balancedbytes.games.ffb.server.db;

/**
 * 
 * @author Kalimar
 */
public enum DbPlayerIconType {
  
  PORTRAIT("PT"),
  HOME_STANDING("HS"),
  HOME_MOVING("HM"),
  AWAY_STANDING("AS"),
  AWAY_MOVING("AM"),
  BASE_PATH("BP");
  
  private String fTypeString;
  
  private DbPlayerIconType(String pTypeString) {
    fTypeString = pTypeString;
  }
  
  public String getTypeString() {
    return fTypeString;
  }
  
  public static DbPlayerIconType fromTypeString(String pTypeString) {
    for (DbPlayerIconType type : values()) {
      if (type.getTypeString().equals(pTypeString)) {
        return type;
      }
    }
    return null;
  }
  
}
