package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.inducement.Prayer;
import com.fumbbl.ffb.inducement.Prayers;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.Scanner;

import java.util.Set;

@FactoryType(FactoryType.Factory.PRAYER)
@RulesCollection(Rules.COMMON)
public class PrayerFactory implements INamedObjectFactory<Prayer> {
	private Prayers prayers;

	public Prayer forName(String pName) {
		for (Prayer prayer : prayers.allPrayers()) {
			if (prayer.getName().equalsIgnoreCase(pName)) {
				return prayer;
			}
		}
		return null;
	}

	public Set<Prayer> allPrayers() {
		return prayers.allPrayers();
	}

	@Override
	public void initialize(Game game) {
		new Scanner<>(Prayers.class).getInstancesImplementing(game.getOptions()).stream().findFirst()
			.ifPresent(instance -> this.prayers = instance);
	}

}
