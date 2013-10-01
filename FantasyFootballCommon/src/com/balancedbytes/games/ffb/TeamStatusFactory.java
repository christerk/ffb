package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class TeamStatusFactory implements IEnumWithIdFactory, IEnumWithNameFactory {

  public TeamStatus forId(int pId) {
    for (TeamStatus status : TeamStatus.values()) {
      if (status.getId() == pId) {
        return status;
      }
    }
    return null;
  }
    
  public TeamStatus forName(String pName) {
    for (TeamStatus status : TeamStatus.values()) {
      if (status.getName().equalsIgnoreCase(pName)) {
        return status;
      }
    }
    return null;
  }

}
