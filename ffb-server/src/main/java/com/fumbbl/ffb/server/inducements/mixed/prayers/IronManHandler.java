package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.AnimationType;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class IronManHandler extends SelectPlayerPrayerHandler {
	@Override
	Prayer handledPrayer() {
		return Prayer.IRON_MAN;
	}

	@Override
	protected PlayerChoiceMode choiceMode() {
		return PlayerChoiceMode.IRON_MAN;
	}

	@Override
	AnimationType animationType() {
		return AnimationType.PRAYER_IRON_MAN;
	}
}
