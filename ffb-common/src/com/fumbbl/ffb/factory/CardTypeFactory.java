package com.fumbbl.ffb.factory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.inducement.CardType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.Scanner;

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
		new Scanner<>(com.fumbbl.ffb.inducement.CardType.class)
			.getClassesImplementing(game.getOptions()).stream().findFirst()
			.ifPresent(cls -> cardTypes.addAll(Arrays.asList(cls.getEnumConstants())));
	}

}
