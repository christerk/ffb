package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class PlayerActionFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public PlayerAction forName(String pName) {
    for (PlayerAction action : PlayerAction.values()) {
      if (action.getName().equalsIgnoreCase(pName)) {
        return action;
      }
    }
    return null;
  }

  public PlayerAction forId(int pId) {
    for (PlayerAction action : PlayerAction.values()) {
      if (action.getId() == pId) {
        return action;
      }
    }
    return null;
  }
  
}
