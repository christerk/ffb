package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public class InjuryTypeFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public InjuryType forName(String pName) {
    for (InjuryType type : InjuryType.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

  public InjuryType forId(int pId) {
    for (InjuryType type : InjuryType.values()) {
      if (type.getId() == pId) {
        return type;
      }
    }
    return null;
  }

}
