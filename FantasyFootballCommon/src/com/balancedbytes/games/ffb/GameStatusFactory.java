package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public class GameStatusFactory implements IEnumWithNameFactory {
  
  public GameStatus forId(int pId) {
    for (GameStatus status : GameStatus.values()) {
      if (status.getId() == pId) {
        return status;
      }
    }
    return null;
  }

  public GameStatus forName(String pName) {
    for (GameStatus status : GameStatus.values()) {
      if (status.getName().equalsIgnoreCase(pName)) {
        return status;
      }
    }
    return null;
  }
  
  public GameStatus forTypeString(String pTypeString) {
    for (GameStatus status : GameStatus.values()) {
      if (status.getTypeString().equals(pTypeString)) {
        return status;
      }
    }
    return null;
  }
  
}
