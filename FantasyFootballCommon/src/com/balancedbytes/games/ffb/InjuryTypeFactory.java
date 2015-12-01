package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public class InjuryTypeFactory implements IEnumWithNameFactory {
  
  public InjuryType forName(String pName) {
    for (InjuryType type : InjuryType.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

}
