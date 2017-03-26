package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class LeaderStateFactory implements INamedObjectFactory {
  
  public LeaderState forName(String pName) {
    for (LeaderState state : LeaderState.values()) {
      if (state.getName().equalsIgnoreCase(pName)) {
        return state;
      }
    }
    return null;
  }

}
