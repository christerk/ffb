package com.fumbbl.ffb.server.inducements.bb2025.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2025.Prayer;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.inducements.mixed.prayers.PrayerHandler;

@RulesCollection(RulesCollection.Rules.BB2025)
public class DazzlingCatchingHandler extends PrayerHandler {

	@Override
	public boolean initEffect(GameState gameState, Team prayingTeam) {
		gameState.getPrayerState().addGetAdditionalCatchesSpp(prayingTeam);
		return true;
	}

	@Override
	public void removeEffectInternal(GameState gameState, Team team) {
	}

	@Override
	public Prayer handledPrayer() {
		return Prayer.DAZZLING_CATCHING;
	}

	@Override
	public AnimationType animationType() {
		return AnimationType.PRAYER_DAZZLING_CATCHING;
	}
}
