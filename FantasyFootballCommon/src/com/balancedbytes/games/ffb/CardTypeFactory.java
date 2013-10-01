package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class CardTypeFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public CardType forName(String pName) {
    for (CardType type : CardType.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

  public CardType forId(int pId) {
    for (CardType type : CardType.values()) {
      if (type.getId() == pId) {
        return type;
      }
    }
    return null;
  }

}
