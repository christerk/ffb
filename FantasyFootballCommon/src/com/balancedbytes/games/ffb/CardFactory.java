package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class CardFactory implements INamedObjectFactory {

	public Card forName(String pName) {
		for (Card card : Card.values()) {
			if (card.getName().equalsIgnoreCase(pName)) {
				return card;
			}
		}
		return null;
	}

	public Card forShortName(String pName) {
		for (Card card : Card.values()) {
			if (card.getShortName().equalsIgnoreCase(pName)) {
				return card;
			}
		}
		return null;
	}

}
