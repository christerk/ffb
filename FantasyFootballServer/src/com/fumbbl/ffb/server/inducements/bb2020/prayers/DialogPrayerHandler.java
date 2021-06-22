package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

import java.util.List;

public abstract class DialogPrayerHandler extends PrayerHandler {

	protected PlayerSelector selector = PlayerSelector.INSTANCE;

	@Override
	final boolean initEffect(GameState gameState, Team prayingTeam) {
		List<Player<?>> players = selector.eligiblePlayers(prayingTeam, gameState.getGame());
		if (players.isEmpty()) {
			return true;
		}
		createDialog(players, gameState, prayingTeam);
		return false;
	}

	protected abstract void createDialog(List<Player<?>> players, GameState gameState, Team prayingTeam);

	@Override
	public final void removeEffect(GameState gameState, Team team) {
		enhancementRemover.removeEnhancement(gameState, team, selector, handledPrayer());
	}

}
