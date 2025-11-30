package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.server.inducements.mixed.prayers.PlayerSelector;

@RulesCollection(RulesCollection.Rules.BB2020)
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
