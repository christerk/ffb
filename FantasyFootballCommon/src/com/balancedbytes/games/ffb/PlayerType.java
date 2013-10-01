package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public enum PlayerType {
  
  REGULAR(1, "Regular"),
  BIG_GUY(2, "Big Guy"),
  STAR(3, "Star"),
  IRREGULAR(4, "Irregular"),
  JOURNEYMAN(5, "Journeyman"),
  RAISED_FROM_DEAD(6, "RaisedFromDead"),
  MERCENARY(7, "Mercenary");
  
  private int fId;
  private String fName;
  
  private PlayerType(int pId, String pName) {
    fId = pId;
    fName = pName;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
    
  public static PlayerType fromId(int pId) {
    for (PlayerType type : values()) {
      if (pId == type.getId()) {
        return type;
      }
    }
    return null;
  }

  public static PlayerType fromName(String pName) {
    if (StringTool.isProvided(pName)) {
      for (PlayerType type : values()) {
        if (pName.equalsIgnoreCase(type.getName())) {
          return type;
        }
      }
      // TODO: clear this up with Christer. should be "Regular" instead
      if (StringTool.isProvided(pName) && pName.equalsIgnoreCase("Normal")) {
        return PlayerType.REGULAR;
      }
    }
    return null;
  }
  
}
