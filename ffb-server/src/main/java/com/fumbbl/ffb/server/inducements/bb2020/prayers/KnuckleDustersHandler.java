package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;

@RulesCollection(RulesCollection.Rules.BB2020)
public class KnuckleDustersHandler extends com.fumbbl.ffb.server.inducements.mixed.prayers.KnuckleDustersHandler {
	@Override
	public Prayer handledPrayer() {
		return Prayer.KNUCKLE_DUSTERS;
	}

	@Override
	public PlayerSelector selector() {
		return PlayerSelector.INSTANCE;
	}
}
