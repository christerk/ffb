package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Game;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StilettoHandler extends RandomSelectionPrayerHandler {
	@Override
	Prayer handledPrayer() {
		return Prayer.STILETTO;
	}

	@Override
	protected int affectedPlayers(Game game) {
		return 1;
	}

	@Override
	protected PlayerSelector selector() {
		return PlayerSelector.INSTANCE;
	}
}
