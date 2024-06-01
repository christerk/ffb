package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

@RulesCollection(RulesCollection.Rules.BB2020)
public class FoulingFrenzyHandler extends PrayerHandler {
	@Override
	Prayer handledPrayer() {
		return Prayer.FOULING_FRENZY;
	}

	@Override
	boolean initEffect(GameState gameState, Team prayingTeam) {
		gameState.getPrayerState().addFoulingFrenzy(prayingTeam);
		return true;
	}

	@Override
	public void removeEffectInternal(GameState gameState, Team team) {
		gameState.getPrayerState().removeFoulingFrenzy(team);
	}

	@Override
	AnimationType animationType() {
		return AnimationType.PRAYER_FOULING_FRENZY;
	}
}
