package com.fumbbl.ffb.factory.bb2020;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.inducement.bb2020.Prayers;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FactoryType(FactoryType.Factory.PRAYER)
@RulesCollection(Rules.BB2020)
public class PrayerFactory implements INamedObjectFactory<Prayer> {
	private Map<Integer, Prayer> prayers;

	public Prayer forName(String pName) {
		for (Prayer prayer : prayers.values()) {
			if (prayer.getName().equalsIgnoreCase(pName)) {
				return prayer;
			}
		}
		return null;
	}

	public Prayer forRoll(int roll) {
		return prayers.get(roll);
	}

	public List<Integer> availablePrayerRolls(InducementSet teamInducements, InducementSet opponentInducements) {
		return prayers.entrySet().stream().filter(entry ->
		{
			Prayer prayer = entry.getValue();
			return !teamInducements.getPrayers().contains(prayer)
				&& !(prayer.affectsBothTeams() && opponentInducements.getPrayers().contains(prayer));
		}).map(Map.Entry::getKey).collect(Collectors.toList());
	}

	@Override
	public void initialize(Game game) {
		boolean useLeagueTable = ((GameOptionBoolean) game.getOptions().getOption(GameOptionId.INDUCEMENT_PRAYERS_USE_LEAGUE_TABLE)).isEnabled();
		Prayers allPrayers = new Prayers();
		prayers = new HashMap<>(allPrayers.getExhibitionPrayers());
		if (useLeagueTable) {
			prayers.putAll(allPrayers.getLeagueOnlyPrayers());
		}
	}
}
