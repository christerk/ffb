package com.fumbbl.ffb.server.inducements.bb2025.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2025.Prayer;
import com.fumbbl.ffb.server.inducements.mixed.prayers.PlayerSelector;

@RulesCollection(RulesCollection.Rules.BB2025)
public class BadHabitsHandler extends com.fumbbl.ffb.server.inducements.mixed.prayers.BadHabitsHandler {
	@Override
	public Prayer handledPrayer() {
		return Prayer.BAD_HABITS;
	}

	@Override
	protected PlayerSelector selector() {
		return OpponentPlayerSelector.INSTANCE;
	}
}
