package com.balancedbytes.games.ffb.inducement.bb2020;

import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.option.GameOptionId;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public enum CardType implements com.balancedbytes.games.ffb.inducement.CardType {

	MAGIC_ITEM("magicItem", "Magic Items Deck", "Magic Item Card", "Magic Item Cards",
		IIconProperty.ANIMATION_CARD_MAGIC_ITEM_FRONT, IIconProperty.ANIMATION_CARD_MAGIC_ITEM_BACK, GameOptionId.CARDS_MAGIC_ITEM_MAX, GameOptionId.CARDS_MAGIC_ITEM_COST),
	DIRTY_TRICK("dirtyTrick", "Dirty Tricks Deck", "Dirty Trick Card", "Dirty Trick Cards",
		IIconProperty.ANIMATION_CARD_DIRTY_TRICK_FRONT, IIconProperty.ANIMATION_CARD_DIRTY_TRICK_BACK, GameOptionId.CARDS_DIRTY_TRICK_MAX, GameOptionId.CARDS_DIRTY_TRICK_COST);

	private final String name, deckName, inducementNameSingle, inducementNameMultiple, cardFront, cardBack;
	private final GameOptionId maxId, costId;

	CardType(String name, String deckName, String inducementNameSingle, String inducementNameMultiple,
	         String cardFront, String cardBack, GameOptionId maxId, GameOptionId costId) {
		this.name = name;
		this.deckName = deckName;
		this.inducementNameSingle = inducementNameSingle;
		this.inducementNameMultiple = inducementNameMultiple;
		this.cardFront = cardFront;
		this.cardBack = cardBack;
		this.maxId = maxId;
		this.costId = costId;
	}

	public String getName() {
		return name;
	}

	public String getDeckName() {
		return deckName;
	}

	public String getInducementNameSingle() {
		return inducementNameSingle;
	}

	public String getInducementNameMultiple() {
		return inducementNameMultiple;
	}

	public GameOptionId getMaxId() {
		return maxId;
	}

	public GameOptionId getCostId() {
		return costId;
	}

	public String getCardFront() {
		return cardFront;
	}

	public String getCardBack() {
		return cardBack;
	}
}
