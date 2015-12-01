package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class CardTypeFactory implements IEnumWithNameFactory {
  
  public CardType forName(String pName) {
    for (CardType type : CardType.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

}
