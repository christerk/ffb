package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class ClientModeFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public ClientMode forName(String pName) {
    for (ClientMode mode : ClientMode.values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

  public ClientMode forId(int pId) {
    for (ClientMode mode : ClientMode.values()) {
      if (mode.getId() == pId) {
        return mode;
      }
    }
    return null;
  }

  public ClientMode forArgument(String pArgument) {
    for (ClientMode mode : ClientMode.values()) {
      if ((mode.getArgument() != null) && mode.getArgument().equalsIgnoreCase(pArgument)) {
        return mode;
      }
    }
    return null;
  }

}
