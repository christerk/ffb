package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.server.GameState;

public abstract class GreasyCleatsHandler extends RandomSelectionPrayerHandler {
	@Override
	protected int affectedPlayers(GameState gameState) {
		return 1;
	}

	@Override
	public AnimationType animationType() {
		return AnimationType.PRAYER_GREASY_CLEATS;
	}
}
