package com.fumbbl.ffb.factory;

import java.util.Set;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.Cards;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.Scanner;

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
