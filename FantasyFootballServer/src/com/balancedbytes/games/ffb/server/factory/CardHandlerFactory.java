package com.balancedbytes.games.ffb.server.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.inducements.CardHandler;
import com.balancedbytes.games.ffb.util.Scanner;

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
		handlers.addAll(new Scanner<>(CardHandler.class).getSubclasses(game.getOptions()));
	}
}
