package com.balancedbytes.games.ffb.server.db.old;

/**
 * 
 * @author Kalimar
 */
public enum DbFieldModelsType {
  
  HOME_PLAYER("HP"),
  AWAY_PLAYER("AP"),
  BALL("BL"),
  BLOODSPOT("BS"),
  TRACK_NUMBER("TN"),
  PUSHBACK_SQUARE("PS"),
  WEATHER("WT"),
  MOVE_SQUARE("MS"),
  DICE_DECORATION("DD"),
  RANGE_RULER("RR"),
  FIELD_MARKER("FM"),
  PLAYER_MARKER("PM"),
  BOMB("BO"),
  HOME_CARDS("HC"),
  AWAY_CARDS("AC");
  
  private String fTypeString;
  
  private DbFieldModelsType(String pTypeString) {
    fTypeString = pTypeString;
  }
  
  public String getTypeString() {
    return fTypeString;
  }
  
  public static DbFieldModelsType fromTypeString(String pTypeString) {
    for (DbFieldModelsType type : values()) {
      if (type.getTypeString().equals(pTypeString)) {
        return type;
      }
    }
    return null;
  }
  
}
