package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class TurnModeFactory implements IEnumWithNameFactory {

  public TurnMode forName(String pName) {
    for (TurnMode mode : TurnMode.values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

}
