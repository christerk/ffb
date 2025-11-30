package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.inducements.mixed.prayers.PrayerHandler;

@RulesCollection(RulesCollection.Rules.BB2020)
public class NecessaryViolenceHandler extends PrayerHandler {
	@Override
	public Prayer handledPrayer() {
		return Prayer.NECESSARY_VIOLENCE;
	}

	@Override
	public boolean initEffect(GameState gameState, Team prayingTeam) {
		gameState.getPrayerState().addGetAdditionalCasSpp(prayingTeam);
		return true;
	}

	@Override
	public void removeEffectInternal(GameState gameState, Team team) {
		gameState.getPrayerState().removeGetAdditionalCasSpp(team);
	}

	@Override
	public AnimationType animationType() {
		return AnimationType.PRAYER_NECESSARY_VIOLENCE;
	}

}
