package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public class CatchScatterThrowInModeFactory implements INamedObjectFactory {
  
  public CatchScatterThrowInMode forName(String pName) {
    for (CatchScatterThrowInMode mode : CatchScatterThrowInMode.values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }
  
}
