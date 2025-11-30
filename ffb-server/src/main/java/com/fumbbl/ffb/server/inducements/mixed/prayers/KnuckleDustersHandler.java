package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.AnimationType;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class KnuckleDustersHandler extends SelectPlayerPrayerHandler {
	@Override
	Prayer handledPrayer() {
		return Prayer.KNUCKLE_DUSTERS;
	}

	@Override
	protected PlayerChoiceMode choiceMode() {
		return PlayerChoiceMode.KNUCKLE_DUSTERS;
	}

	@Override
	AnimationType animationType() {
		return AnimationType.PRAYER_KNUCKLE_DUSTERS;
	}
}
