package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.bb2020.ReportPlayerEvent;
import com.fumbbl.ffb.server.GameState;

import java.util.Arrays;
import java.util.List;

public abstract class RandomSelectionPrayerHandler extends PrayerHandler {

	protected abstract int affectedPlayers(Game game);

	protected abstract PlayerSelector selector();

	@Override
	boolean initEffect(GameState gameState, Team prayingTeam) {
		List<Player<?>> players = selector().selectPlayers(prayingTeam, gameState.getGame(), affectedPlayers(gameState.getGame()));
		applyEffect(players, gameState.getGame());
		return true;
	}

	private void applyEffect(List<Player<?>> players, Game game) {
		players.forEach(player -> {
			game.getFieldModel().addPrayerEnhancements(player, handledPrayer());
			reports.add(new ReportPlayerEvent(player.getId(), handledPrayer().eventMessage()));
		});
	}

	@Override
	public void removeEffect(GameState gameState, Team team) {
		Arrays.stream(selector().determineTeam(team, gameState.getGame()).getPlayers())
			.forEach(player -> gameState.getGame().getFieldModel().removePrayerEnhancements(player, handledPrayer()));
	}
}
