package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class InducementTypeFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public InducementType forName(String pName) {
    for (InducementType type : InducementType.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

  public InducementType forId(int pId) {
    for (InducementType type : InducementType.values()) {
      if (type.getId() == pId) {
        return type;
      }
    }
    return null;
  }

}
