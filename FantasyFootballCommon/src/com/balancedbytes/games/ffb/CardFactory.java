package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class CardFactory implements IEnumWithIdFactory, IEnumWithNameFactory {

  public Card forId(int pId) {
    for (Card card : Card.values()) {
      if (card.getId() == pId) {
        return card;
      }
    }
    return null;
  }
    
  public Card forName(String pName) {
    for (Card card : Card.values()) {
      if (card.getName().equalsIgnoreCase(pName)) {
        return card;
      }
    }
    return null;
  }

}
