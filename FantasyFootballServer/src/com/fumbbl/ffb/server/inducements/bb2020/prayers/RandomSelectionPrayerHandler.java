package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.bb2020.ReportPlayerEvent;
import com.fumbbl.ffb.server.GameState;

import java.util.List;

public abstract class RandomSelectionPrayerHandler extends PrayerHandler {

	protected abstract int affectedPlayers(GameState gameState);

	protected abstract PlayerSelector selector();

	@Override
	final boolean initEffect(GameState gameState, Team prayingTeam) {
		List<Player<?>> players = selector().selectPlayers(prayingTeam, gameState.getGame(), affectedPlayers(gameState));
		players.forEach(player -> {
			gameState.getGame().getFieldModel().addPrayerEnhancements(player, handledPrayer());
			reports.add(new ReportPlayerEvent(player.getId(), handledPrayer().eventMessage()));
		});
		return true;
	}

	@Override
	public final void removeEffect(GameState gameState, Team team) {
		enhancementRemover.removeEnhancement(gameState, team, selector(), handledPrayer());
	}
}
