package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.AnimationType;

@RulesCollection(RulesCollection.Rules.BB2020)
public class BlessedStatueOfNuffleHandler extends SelectPlayerPrayerHandler {
	@Override
	Prayer handledPrayer() {
		return Prayer.BLESSED_STATUE_OF_NUFFLE;
	}

	@Override
	AnimationType animationType() {
		return AnimationType.PRAYER_BLESSED_STATUE_OF_NUFFLE;
	}

	@Override
	protected PlayerChoiceMode choiceMode() {
		return PlayerChoiceMode.BLESSED_STATUE_OF_NUFFLE;
	}
}
