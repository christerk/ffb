package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

public class OpponentPlayerSelector extends PlayerSelector {

	static PlayerSelector INSTANCE = new OpponentPlayerSelector();

	@Override
	protected Team determineTeam(Team team, Game game) {
		return game.getOtherTeam(team);
	}
}
