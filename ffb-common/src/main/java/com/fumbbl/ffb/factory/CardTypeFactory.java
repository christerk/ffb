package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.inducement.CardType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.Scanner;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CARD_TYPE)
@RulesCollection(Rules.COMMON)
public class CardTypeFactory implements INamedObjectFactory<CardType> {

	private final Set<com.fumbbl.ffb.inducement.CardType> cardTypes = new HashSet<>();

	public CardType forName(String pName) {
		return cardTypes.stream().filter(type -> type.getName().equalsIgnoreCase(pName))
			.findFirst().orElse(null);
	}

	public Set<CardType> getCardTypes() {
		return cardTypes;
	}

	@Override
	public void initialize(Game game) {
		cardTypes.addAll(new Scanner<>(CardType.class).getEnumValues(game.getOptions()));
	}
}
