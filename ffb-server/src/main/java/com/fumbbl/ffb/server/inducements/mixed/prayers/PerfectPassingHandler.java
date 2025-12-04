package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

public abstract class PerfectPassingHandler extends PrayerHandler {
		@Override
	public boolean initEffect(GameState gameState, Team prayingTeam) {
		gameState.getPrayerState().addGetAdditionalCompletionSpp(prayingTeam);
		return true;
	}

	@Override
	public void removeEffectInternal(GameState gameState, Team team) {
		gameState.getPrayerState().removeGetAdditionalCompletionSpp(team);
	}

	@Override
	public AnimationType animationType() {
		return AnimationType.PRAYER_PERFECT_PASSING;
	}

}
