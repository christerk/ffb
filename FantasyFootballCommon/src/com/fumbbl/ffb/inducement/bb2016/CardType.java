package com.fumbbl.ffb.inducement.bb2016;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.option.GameOptionId;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public enum CardType implements com.fumbbl.ffb.inducement.CardType {

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

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDeckName() {
		return deckName;
	}

	@Override
	public String getInducementNameSingle() {
		return inducementNameSingle;
	}

	@Override
	public String getInducementNameMultiple() {
		return inducementNameMultiple;
	}

	@Override
	public GameOptionId getMaxId() {
		return maxId;
	}

	@Override
	public GameOptionId getCostId() {
		return costId;
	}

	@Override
	public String getCardFront() {
		return cardFront;
	}

	@Override
	public String getCardBack() {
		return cardBack;
	}
}
