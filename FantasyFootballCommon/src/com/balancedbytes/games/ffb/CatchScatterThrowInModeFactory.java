package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public class CatchScatterThrowInModeFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public CatchScatterThrowInMode forId(int pId) {
    if (pId > 0) {
      for (CatchScatterThrowInMode mode : CatchScatterThrowInMode.values()) {
        if (mode.getId() == pId) {
          return mode;
        }
      }
    }
    return null;
  }
  
  public CatchScatterThrowInMode forName(String pName) {
    for (CatchScatterThrowInMode mode : CatchScatterThrowInMode.values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }
  
}
