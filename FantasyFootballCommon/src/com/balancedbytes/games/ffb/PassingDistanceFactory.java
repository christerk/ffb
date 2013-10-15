package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class PassingDistanceFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public PassingDistance forName(String pName) {
    for (PassingDistance distance : PassingDistance.values()) {
      if (distance.getName().equalsIgnoreCase(pName)) {
        return distance;
      }
    }
    return null;
  }

  public PassingDistance forId(int pId) {
    for (PassingDistance distance : PassingDistance.values()) {
      if (distance.getId() == pId) {
        return distance;
      }
    }
    return null;
  }

  public PassingDistance forShortcut(char pShortcut) {
    for (PassingDistance distance : PassingDistance.values()) {
      if (distance.getShortcut() == pShortcut) {
        return distance;
      }
    }
    return null;
  }
  
}
