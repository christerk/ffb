package com.fumbbl.ffb.server.inducements.bb2025.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2025.Prayer;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.inducements.mixed.prayers.RandomSelectionPrayerHandler;

@RulesCollection(RulesCollection.Rules.BB2025)
public class BlessedStatueOfNuffleHandler extends RandomSelectionPrayerHandler {
	@Override
	public Prayer handledPrayer() {
		return Prayer.BLESSING_OF_NUFFLE;
	}

	@Override
	protected int affectedPlayers(GameState gameState) {
		return 1;
	}

	@Override
	public PlayerSelector selector() {
		return PlayerSelector.INSTANCE;
	}

	@Override
	public AnimationType animationType() {
		return AnimationType.PRAYER_BLESSED_STATUE_OF_NUFFLE;
	}
}
