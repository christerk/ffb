package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.bb2020.ReportPrayerWasted;
import com.fumbbl.ffb.server.GameState;

import java.util.List;

public abstract class DialogPrayerHandler extends PrayerHandler {

	protected PlayerSelector selector = PlayerSelector.INSTANCE;

	@Override
	final boolean initEffect(GameState gameState, Team prayingTeam) {
		List<Player<?>> players = selector.eligiblePlayers(prayingTeam, gameState.getGame());
		if (players.isEmpty()) {
			reports.add(new ReportPrayerWasted(this.handledPrayer().getName()));
			return true;
		}
		createDialog(players, gameState, prayingTeam);
		return handled(gameState.getGame());
	}

	protected abstract void createDialog(List<Player<?>> players, GameState gameState, Team prayingTeam);

	protected abstract boolean handled(Game game);

	@Override
	public final void removeEffect(GameState gameState, Team team) {
		enhancementRemover.removeEnhancement(gameState, team, selector, handledPrayer());
	}

}
