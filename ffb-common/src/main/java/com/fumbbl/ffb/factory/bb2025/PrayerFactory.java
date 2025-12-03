package com.fumbbl.ffb.factory.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.inducement.Prayer;
import com.fumbbl.ffb.inducement.bb2025.Prayers;
import com.fumbbl.ffb.model.Game;

import java.util.HashMap;

@FactoryType(FactoryType.Factory.PRAYER)
@RulesCollection(Rules.BB2025)
public class PrayerFactory extends com.fumbbl.ffb.factory.PrayerFactory {

	@Override
	public void initialize(Game game) {
		Prayers allPrayers = new Prayers();
		prayers = new HashMap<>(allPrayers.getAllPrayers());
	}

	@Override
	public Prayer intensivePrayer() {
		return com.fumbbl.ffb.inducement.bb2025.Prayer.INTENSIVE_TRAINING;
	}

	@Override
	public Prayer valueOf(String enumName) {
		return com.fumbbl.ffb.inducement.bb2025.Prayer.valueOf(enumName);
	}
}
