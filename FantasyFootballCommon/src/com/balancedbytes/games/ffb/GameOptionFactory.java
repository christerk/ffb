package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public class GameOptionFactory implements IEnumWithNameFactory {
  
  public GameOption forName(String pName) {
    for (GameOption mode : GameOption.values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

}
