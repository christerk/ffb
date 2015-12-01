package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class PlayerActionFactory implements IEnumWithNameFactory {
  
  public PlayerAction forName(String pName) {
    for (PlayerAction action : PlayerAction.values()) {
      if (action.getName().equalsIgnoreCase(pName)) {
        return action;
      }
    }
    return null;
  }
  
}
