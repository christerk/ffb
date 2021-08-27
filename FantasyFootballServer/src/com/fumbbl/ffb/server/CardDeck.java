package com.fumbbl.ffb.server;

import java.util.ArrayList;
import java.util.List;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.CardFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardType;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class CardDeck {

	private CardType fType;
	private List<Card> fCards;

	public CardDeck(CardType pType) {
		fType = pType;
		fCards = new ArrayList<>();
	}

	public CardType getType() {
		return fType;
	}

	public void add(Card pCard) {
		if ((pCard == null) || (pCard.getType() != getType())) {
			return;
		}
		fCards.add(pCard);
	}

	public boolean remove(Card pCard) {
		if (pCard == null) {
			return false;
		}
		return fCards.remove(pCard);
	}

	public Card draw(int pIndex) {
		return fCards.remove(pIndex);
	}

	public int size() {
		return fCards.size();
	}

	public List<Card> getCards() {
		return fCards;
	}

	public void build(Game pGame) {
		CardFactory factory = pGame.getFactory(FactoryType.Factory.CARD);
		for (Card card : factory.allCards()) {
			add(card);
		}
		for (Card card : pGame.getTurnDataHome().getInducementSet().getAvailableCards()) {
			remove(card);
		}
		for (Card card : pGame.getTurnDataAway().getInducementSet().getAvailableCards()) {
			remove(card);
		}
	}

}
