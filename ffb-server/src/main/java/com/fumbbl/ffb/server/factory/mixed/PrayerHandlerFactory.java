package com.fumbbl.ffb.server.factory.mixed;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.inducements.mixed.prayers.PrayerHandler;
import com.fumbbl.ffb.util.Scanner;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@FactoryType(FactoryType.Factory.PRAYER_HANDLER)
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class PrayerHandlerFactory implements INamedObjectFactory<PrayerHandler> {

	private final Set<PrayerHandler> handlers = new HashSet<>();

	@Override
	public PrayerHandler forName(String pName) {
		return handlers.stream().filter(handler -> handler.getName().equalsIgnoreCase(pName)).findFirst().orElse(null);
	}

	public Optional<PrayerHandler> forPrayer(Prayer prayer) {
		return handlers.stream().filter(handler -> handler.handles(prayer)).findFirst();
	}

	@Override
	public void initialize(Game game) {
		handlers.addAll(new Scanner<>(PrayerHandler.class).getSubclassInstances(game.getOptions()));
	}
}
