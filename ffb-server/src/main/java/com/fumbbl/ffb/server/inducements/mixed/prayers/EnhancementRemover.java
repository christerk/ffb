package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

import java.util.Arrays;

public class EnhancementRemover {

	public void removeEnhancement(GameState gameState, Team team, PlayerSelector selector, Prayer prayer) {
		Arrays.stream(selector.determineTeam(team, gameState.getGame()).getPlayers())
			.forEach(player -> gameState.getGame().getFieldModel().removePrayerEnhancements(player, prayer));
	}
}
