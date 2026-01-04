package com.fumbbl.ffb.model;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.KickoffResultFactory;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.factory.PrayerFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EnhancementRegistry {
	private final Map<String, TemporaryEnhancements> mapping = new HashMap<>();

	public EnhancementRegistry(Game game) {
		MechanicsFactory mechanicsFactory = game.getFactory(FactoryType.Factory.MECHANIC);
		StatsMechanic statsMechanic = (StatsMechanic) mechanicsFactory.forName(Mechanic.Type.STAT.name());

		PrayerFactory prayerFactory = game.getFactory(FactoryType.Factory.PRAYER);
		if (prayerFactory != null) {
			prayerFactory.allPrayerRolls().stream().map(prayerFactory::forRoll)
				.forEach(prayer -> mapping.put(prayer.getName(), prayer.enhancements(statsMechanic)));

		}

		KickoffResultFactory kickoffResultFactory = game.getFactory(FactoryType.Factory.KICKOFF_RESULT);
		if (kickoffResultFactory != null) {
			kickoffResultFactory.allResults()
				.forEach(result -> mapping.put(result.getName(), result.enhancements(statsMechanic)));
		}
	}

	public Optional<TemporaryEnhancements> forName(String name) {
		return Optional.ofNullable(mapping.get(name));
	}
}
