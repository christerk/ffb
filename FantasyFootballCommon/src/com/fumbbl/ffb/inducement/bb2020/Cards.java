package com.fumbbl.ffb.inducement.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Card;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
public class Cards implements com.fumbbl.ffb.inducement.Cards {
	private final Set<Card> cards = new HashSet<>() ;

	@Override
	public Set<Card> allCards() {
		return cards;
	}
}
