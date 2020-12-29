package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.card)
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

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
