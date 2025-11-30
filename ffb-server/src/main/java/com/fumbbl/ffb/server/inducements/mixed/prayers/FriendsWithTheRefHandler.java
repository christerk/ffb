package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class FriendsWithTheRefHandler extends PrayerHandler {

	@Override
	Prayer handledPrayer() {
		return Prayer.FRIENDS_WITH_THE_REF;
	}

	@Override
	boolean initEffect(GameState gameState, Team prayingTeam) {
		gameState.getPrayerState().addFriendsWithRef(prayingTeam);
		return true;
	}

	@Override
	public void removeEffectInternal(GameState gameState, Team team) {
		gameState.getPrayerState().removeFriendsWithRef(team);
	}

	@Override
	AnimationType animationType() {
		return AnimationType.PRAYER_FRIENDS_WITH_THE_REF;
	}
}
