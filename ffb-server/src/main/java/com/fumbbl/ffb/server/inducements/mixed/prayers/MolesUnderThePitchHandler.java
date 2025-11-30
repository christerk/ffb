package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

public abstract class MolesUnderThePitchHandler extends PrayerHandler {
	@Override
	public boolean initEffect(GameState gameState, Team prayingTeam) {
		gameState.getPrayerState().addMolesUnderThePitch(prayingTeam);
		return true;
	}

	@Override
	public void removeEffectInternal(GameState gameState, Team team) {
		gameState.getPrayerState().removeMolesUnderThePitch(team);
	}

	@Override
	public AnimationType animationType() {
		return AnimationType.PRAYER_MOLES_UNDER_THE_PITCH;
	}

}
