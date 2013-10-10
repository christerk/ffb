package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class PushbackModeFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public PushbackMode forName(String pName) {
    for (PushbackMode mode : PushbackMode.values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

  public PushbackMode forId(int pId) {
    for (PushbackMode mode : PushbackMode.values()) {
      if (mode.getId() == pId) {
        return mode;
      }
    }
    return null;
  }

}
