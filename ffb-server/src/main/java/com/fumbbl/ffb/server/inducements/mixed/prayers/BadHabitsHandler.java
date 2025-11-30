package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.server.GameState;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class BadHabitsHandler extends RandomSelectionPrayerHandler {
	@Override
	Prayer handledPrayer() {
		return Prayer.BAD_HABITS;
	}

	@Override
	protected int affectedPlayers(GameState gameState) {
		return gameState.getDiceRoller().rollDice(3);
	}

	@Override
	protected PlayerSelector selector() {
		return OpponentPlayerSelector.INSTANCE;
	}

	@Override
	AnimationType animationType() {
		return AnimationType.PRAYER_BAD_HABITS;
	}
}
