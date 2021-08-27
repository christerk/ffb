package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

@RulesCollection(RulesCollection.Rules.BB2020)
public class NecessaryViolenceHandler extends PrayerHandler {
	@Override
	Prayer handledPrayer() {
		return Prayer.NECESSARY_VIOLENCE;
	}

	@Override
	boolean initEffect(GameState gameState, Team prayingTeam) {
		gameState.getPrayerState().addGetAdditionalCasSpp(prayingTeam);
		return true;
	}

	@Override
	public void removeEffect(GameState gameState, Team team) {
		gameState.getPrayerState().removeGetAdditionalCasSpp(team);
	}
}
