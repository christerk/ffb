package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class ConcedeGameStatusFactory implements IEnumWithNameFactory {
  
  public ConcedeGameStatus forName(String pName) {
    for (ConcedeGameStatus status : ConcedeGameStatus.values()) {
      if (status.getName().equalsIgnoreCase(pName)) {
        return status;
      }
    }
    return null;
  }

}
