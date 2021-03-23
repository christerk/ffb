package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.Cards;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Set;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CARD)
@RulesCollection(Rules.COMMON)
public class CardFactory implements INamedObjectFactory<Card> {
	private Cards cards;

	public Card forName(String pName) {
		for (Card card : cards.allCards()) {
			if (card.getName().equalsIgnoreCase(pName)) {
				return card;
			}
		}
		return null;
	}

	public Card forShortName(String pName) {
		for (Card card : cards.allCards()) {
			if (card.getShortName().equalsIgnoreCase(pName)) {
				return card;
			}
		}
		return null;
	}

	public Set<Card> allCards() {
		return cards.allCards();
	}

	@Override
	public void initialize(Game game) {
		new Scanner<>(Cards.class).getInstancesImplementing(game.getOptions()).stream().findFirst()
			.ifPresent(instance -> this.cards = instance);
	}

}
