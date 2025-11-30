package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;

@RulesCollection(RulesCollection.Rules.BB2020)
public class FriendsWithTheRefHandler extends com.fumbbl.ffb.server.inducements.mixed.prayers.FriendsWithTheRefHandler {

	@Override
	public Prayer handledPrayer() {
		return Prayer.FRIENDS_WITH_THE_REF;
	}
}
