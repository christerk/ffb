package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

import java.util.Arrays;
import java.util.List;

@FactoryType(FactoryType.Factory.RE_ROLL_PROPERTY)
@RulesCollection(Rules.BB2025)
public class ReRollPropertyFactory implements INamedObjectFactory<ReRollProperty> {

	/**
	 * Former per-type drive bonus properties, still present in replays and stored game states.
	 */
	private static final List<String> LEGACY_DRIVE_BONUS_NAMES =
		Arrays.asList("BRILLIANT_COACHING", "PUMP_UP_THE_CROWD", "SHOW_STAR");

	public ReRollProperty forName(String pName) {
		if (LEGACY_DRIVE_BONUS_NAMES.stream().anyMatch(name -> name.equalsIgnoreCase(pName))) {
			return ReRollProperty.DRIVE_BONUS;
		}
		return Arrays.stream(ReRollProperty.values()).filter(prop -> prop.getName().equalsIgnoreCase(pName)).findFirst()
				.orElse(null);
	}

	@Override
	public void initialize(Game game) {
	}

}
