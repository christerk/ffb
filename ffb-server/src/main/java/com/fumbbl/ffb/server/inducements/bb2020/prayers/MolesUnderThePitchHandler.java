package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;

@RulesCollection(RulesCollection.Rules.BB2020)
public class MolesUnderThePitchHandler extends com.fumbbl.ffb.server.inducements.mixed.prayers.MolesUnderThePitchHandler {
	@Override
	public Prayer handledPrayer() {
		return Prayer.MOLES_UNDER_THE_PITCH;
	}
}
