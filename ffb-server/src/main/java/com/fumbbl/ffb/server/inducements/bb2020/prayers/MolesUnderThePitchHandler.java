package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

@RulesCollection(RulesCollection.Rules.BB2020)
public class MolesUnderThePitchHandler extends PrayerHandler {
	@Override
	Prayer handledPrayer() {
		return Prayer.MOLES_UNDER_THE_PITCH;
	}

	@Override
	boolean initEffect(GameState gameState, Team prayingTeam) {
		gameState.getPrayerState().addMolesUnderThePitch(prayingTeam);
		return true;
	}

	@Override
	public void removeEffectInternal(GameState gameState, Team team) {
		gameState.getPrayerState().removeMolesUnderThePitch(team);
	}

	@Override
	AnimationType animationType() {
		return AnimationType.PRAYER_MOLES_UNDER_THE_PITCH;
	}

}
