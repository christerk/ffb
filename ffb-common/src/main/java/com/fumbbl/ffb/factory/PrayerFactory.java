package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.inducement.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public abstract class PrayerFactory implements INamedObjectFactory<Prayer> {
	protected Map<Integer, Prayer> prayers;

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

	public List<Integer> allPrayerRolls() {
		return new ArrayList<>(prayers.keySet());
	}

	public List<Integer> availablePrayerRolls(InducementSet teamInducements, InducementSet opponentInducements) {
		return prayers.entrySet().stream().filter(entry -> {
			Prayer prayer = entry.getValue();
			return !teamInducements.getPrayers().contains(prayer) &&
				!(prayer.affectsBothTeams() && opponentInducements.getPrayers().contains(prayer));
		}).map(Map.Entry::getKey).collect(Collectors.toList());
	}

	public List<Prayer> sort(Set<Prayer> unsortedPrayers) {
		return prayers.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).map(Map.Entry::getValue)
			.filter(unsortedPrayers::contains).collect(Collectors.toList());
	}

	public abstract Prayer intensivePrayer();

	public abstract Prayer valueOf(String enumName);

	@Override
	public abstract void initialize(Game game);
}
