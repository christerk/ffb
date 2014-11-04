package com.balancedbytes.games.ffb.old;

import com.balancedbytes.games.ffb.IEnumWithNameFactory;


/**
 * 
 * @author Kalimar
 */
public class GameOptionFactoryOld implements IEnumWithNameFactory {
  
  public GameOptionOld forName(String pName) {
    for (GameOptionOld mode : GameOptionOld.values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

}
