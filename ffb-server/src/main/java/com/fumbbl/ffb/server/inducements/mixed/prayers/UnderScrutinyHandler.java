package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

public abstract class UnderScrutinyHandler extends PrayerHandler {
	@Override
	public boolean initEffect(GameState gameState, Team prayingTeam) {
		gameState.getPrayerState().addUnderScrutiny(gameState.getGame().getOtherTeam(prayingTeam));
		return true;
	}

	@Override
	public void removeEffectInternal(GameState gameState, Team team) {
		gameState.getPrayerState().removeUnderScrutiny(gameState.getGame().getOtherTeam(team));
	}

	@Override
	public AnimationType animationType() {
		return AnimationType.PRAYER_UNDER_SCRUTINY;
	}
}
