package com.fumbbl.ffb.server.inducements.bb2025.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2025.Prayer;

@RulesCollection(RulesCollection.Rules.BB2025)
public class UnderScrutinyHandler extends com.fumbbl.ffb.server.inducements.mixed.prayers.UnderScrutinyHandler {
	@Override
	public Prayer handledPrayer() {
		return Prayer.UNDER_SCRUTINY;
	}
}
