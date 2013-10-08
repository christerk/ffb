package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class TurnModeFactory implements IEnumWithIdFactory, IEnumWithNameFactory {

  public TurnMode forId(int pId) {
    if (pId > 0) {
      for (TurnMode mode : TurnMode.values()) {
        if (mode.getId() == pId) {
          return mode;
        }
      }
    }
    return null;
  }
    
  public TurnMode forName(String pName) {
    for (TurnMode mode : TurnMode.values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

}
