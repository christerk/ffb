package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class LeaderStateFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public LeaderState forName(String pName) {
    for (LeaderState state : LeaderState.values()) {
      if (state.getName().equalsIgnoreCase(pName)) {
        return state;
      }
    }
    return null;
  }

  public LeaderState forId(int pId) {
    for (LeaderState state : LeaderState.values()) {
      if (state.getId() == pId) {
        return state;
      }
    }
    return null;
  }

}
