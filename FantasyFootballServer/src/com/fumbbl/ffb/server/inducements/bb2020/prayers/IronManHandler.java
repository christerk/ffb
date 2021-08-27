package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;

@RulesCollection(RulesCollection.Rules.BB2020)
public class IronManHandler extends SelectPlayerPrayerHandler {
	@Override
	Prayer handledPrayer() {
		return Prayer.IRON_MAN;
	}

	@Override
	protected PlayerChoiceMode choiceMode() {
		return PlayerChoiceMode.IRON_MAN;
	}
}
