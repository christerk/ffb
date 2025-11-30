package com.fumbbl.ffb.factory.bb2020;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.inducement.Prayer;
import com.fumbbl.ffb.inducement.bb2020.Prayers;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;

import java.util.HashMap;

@FactoryType(FactoryType.Factory.PRAYER)
@RulesCollection(Rules.BB2020)
public class PrayerFactory extends com.fumbbl.ffb.factory.PrayerFactory {

	@Override
	public void initialize(Game game) {
		boolean useLeagueTable = ((GameOptionBoolean) game.getOptions()
			.getOptionWithDefault(GameOptionId.INDUCEMENT_PRAYERS_USE_LEAGUE_TABLE)).isEnabled();
		Prayers allPrayers = new Prayers();
		prayers = new HashMap<>(allPrayers.getExhibitionPrayers());
		if (useLeagueTable) {
			prayers.putAll(allPrayers.getLeagueOnlyPrayers());
		}
	}

	@Override
	public Prayer intensivePrayer() {
		return com.fumbbl.ffb.inducement.bb2020.Prayer.INTENSIVE_TRAINING;
	}

	@Override
	public Prayer valueOf(String enumName) {
		return com.fumbbl.ffb.inducement.bb2020.Prayer.valueOf(enumName);
	}
}
