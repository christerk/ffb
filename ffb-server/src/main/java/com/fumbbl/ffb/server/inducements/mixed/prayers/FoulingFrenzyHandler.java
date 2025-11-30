package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

public abstract class FoulingFrenzyHandler extends PrayerHandler {
	@Override
	public boolean initEffect(GameState gameState, Team prayingTeam) {
		gameState.getPrayerState().addFoulingFrenzy(prayingTeam);
		return true;
	}

	@Override
	public void removeEffectInternal(GameState gameState, Team team) {
		gameState.getPrayerState().removeFoulingFrenzy(team);
	}

	@Override
	public AnimationType animationType() {
		return AnimationType.PRAYER_FOULING_FRENZY;
	}
}
