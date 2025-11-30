package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.mixed.ReportPlayerEvent;
import com.fumbbl.ffb.report.mixed.ReportPrayerWasted;
import com.fumbbl.ffb.server.GameState;

import java.util.List;

public abstract class RandomSelectionPrayerHandler extends PrayerHandler {

	protected abstract int affectedPlayers(GameState gameState);

	protected abstract PlayerSelector selector();

	@Override
	final boolean initEffect(GameState gameState, Team prayingTeam) {
		List<Player<?>> players = selector().selectPlayers(prayingTeam, gameState.getGame(), affectedPlayers(gameState));
		if (players.isEmpty()) {
			reports.add(new ReportPrayerWasted(this.handledPrayer().getName()));
		}
		players.forEach(player -> {
			gameState.getGame().getFieldModel().addPrayerEnhancements(player, handledPrayer());
			reports.add(new ReportPlayerEvent(player.getId(), handledPrayer().eventMessage()));
		});
		return true;
	}

	@Override
	public final void removeEffectInternal(GameState gameState, Team team) {
		enhancementRemover.removeEnhancement(gameState, team, selector(), handledPrayer());
	}
}
