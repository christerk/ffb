package com.fumbbl.ffb.server.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.inducements.CardHandler;
import com.fumbbl.ffb.util.Scanner;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@FactoryType(FactoryType.Factory.CARD_HANDLER)
@RulesCollection(RulesCollection.Rules.COMMON)
public class CardHandlerFactory implements INamedObjectFactory<CardHandler> {

	private final Set<CardHandler> handlers = new HashSet<>();

	@Override
	public CardHandler forName(String pName) {
		return handlers.stream().filter(handler -> handler.getName().equalsIgnoreCase(pName)).findFirst().orElse(null);
	}

	public Optional<CardHandler> forCard(Card card) {
		return handlers.stream().filter(handler -> handler.isResponsible(card)).findFirst();
	}

	@Override
	public void initialize(Game game) {
		handlers.addAll(new Scanner<>(CardHandler.class).getSubclassInstances(game.getOptions()));
	}
}
