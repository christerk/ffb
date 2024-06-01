package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.server.GameState;

@RulesCollection(RulesCollection.Rules.BB2020)
public class GreasyCleatsHandler extends RandomSelectionPrayerHandler {
	@Override
	Prayer handledPrayer() {
		return Prayer.GREASY_CLEATS;
	}

	@Override
	protected int affectedPlayers(GameState gameState) {
		return 1;
	}

	@Override
	protected PlayerSelector selector() {
		return OpponentPlayerSelector.INSTANCE;
	}

	@Override
	AnimationType animationType() {
		return AnimationType.PRAYER_GREASY_CLEATS;
	}
}
