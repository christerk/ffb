package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class ClientStateIdFactory implements IEnumWithNameFactory {
  
  public ClientStateId forName(String pName) {
    for (ClientStateId state : ClientStateId.values()) {
      if (state.getName().equalsIgnoreCase(pName)) {
        return state;
      }
    }
    return null;
  }

}
