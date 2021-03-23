package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.inducement.CardType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CARD_TYPE)
@RulesCollection(Rules.COMMON)
public class CardTypeFactory implements INamedObjectFactory<CardType> {

	private final Set<com.balancedbytes.games.ffb.inducement.CardType> cardTypes = new HashSet<>();

	public CardType forName(String pName) {
		return cardTypes.stream().filter(type -> type.getName().equalsIgnoreCase(pName))
			.findFirst().orElse(null);
	}

	public Set<CardType> getCardTypes() {
		return cardTypes;
	}

	@Override
	public void initialize(Game game) {
		new Scanner<>(com.balancedbytes.games.ffb.inducement.CardType.class)
			.getClassesImplementing(game.getOptions()).stream().findFirst()
			.ifPresent(cls -> cardTypes.addAll(Arrays.asList(cls.getEnumConstants())));
	}

}
