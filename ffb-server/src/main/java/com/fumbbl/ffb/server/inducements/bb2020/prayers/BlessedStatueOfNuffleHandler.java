package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;

@RulesCollection(RulesCollection.Rules.BB2020)
public class BlessedStatueOfNuffleHandler extends com.fumbbl.ffb.server.inducements.mixed.prayers.BlessedStatueOfNuffleHandler {
	@Override
	public Prayer handledPrayer() {
		return Prayer.BLESSED_STATUE_OF_NUFFLE;
	}

	@Override
	public PlayerSelector selector() {
		return PlayerSelector.INSTANCE;
	}
}
