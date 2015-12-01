package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class InducementTypeFactory implements IEnumWithNameFactory {
  
  public InducementType forName(String pName) {
    for (InducementType type : InducementType.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

}
