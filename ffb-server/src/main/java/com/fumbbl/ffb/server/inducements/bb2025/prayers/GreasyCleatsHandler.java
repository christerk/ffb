package com.fumbbl.ffb.server.inducements.bb2025.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2025.Prayer;
import com.fumbbl.ffb.server.inducements.mixed.prayers.PlayerSelector;

@RulesCollection(RulesCollection.Rules.BB2025)
public class GreasyCleatsHandler extends com.fumbbl.ffb.server.inducements.mixed.prayers.GreasyCleatsHandler {
	@Override
	public Prayer handledPrayer() {
		return Prayer.GREASY_CLEATS;
	}

	@Override
	protected PlayerSelector selector() {
		return OpponentPlayerSelector.INSTANCE;
	}
}
