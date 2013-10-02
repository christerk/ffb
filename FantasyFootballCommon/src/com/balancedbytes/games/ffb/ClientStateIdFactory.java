package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class ClientStateIdFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public ClientStateId forName(String pName) {
    for (ClientStateId state : ClientStateId.values()) {
      if (state.getName().equalsIgnoreCase(pName)) {
        return state;
      }
    }
    return null;
  }

  public ClientStateId forId(int pId) {
    for (ClientStateId state : ClientStateId.values()) {
      if (state.getId() == pId) {
        return state;
      }
    }
    return null;
  }
  
}
