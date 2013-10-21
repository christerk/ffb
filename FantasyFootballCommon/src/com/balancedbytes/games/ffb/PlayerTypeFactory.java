package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class PlayerTypeFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public PlayerType forName(String pName) {
    if (StringTool.isProvided(pName)) {
      for (PlayerType type : PlayerType.values()) {
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

  public PlayerType forId(int pId) {
    if (pId > 0) {
      for (PlayerType type : PlayerType.values()) {
        if (pId == type.getId()) {
          return type;
        }
      }
    }
    return null;
  }
  
}
