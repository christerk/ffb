package com.fumbbl.ffb;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.ZappedPlayer;

public enum ApothecaryType {
	TEAM("Team Apothecary"), WANDERING("Wandering Apothecary"), PLAGUE("Plague Doctor");

	private final String name;

	ApothecaryType(String name) {
		this.name = name;
	}

	public static ApothecaryType forPlayer(Game game, Player<?> defender) {
		if (defender instanceof ZappedPlayer || defender.getPlayerType() == PlayerType.STAR) {
			return null;
		}

		boolean teamHasWanderingApo = (game.getTeamHome().hasPlayer(defender) && game.getTurnDataHome().getWanderingApothecaries() > 0)
			|| (game.getTeamAway().hasPlayer(defender) && game.getTurnDataAway().getWanderingApothecaries() > 0);

		boolean teamHasPlagueDoctor = (game.getTeamHome().hasPlayer(defender) && game.getTurnDataHome().getPlagueDoctors() > 0)
			|| (game.getTeamAway().hasPlayer(defender) && game.getTurnDataAway().getPlagueDoctors() > 0);

		if (defender.getPlayerType() == PlayerType.MERCENARY) {
			if (teamHasWanderingApo) {
				return WANDERING;
			}
		} else if (defender.isJourneyman()) {

			if (teamHasWanderingApo) {
				return WANDERING;
			}

			if (teamHasPlagueDoctor) {
				return PLAGUE;
			}
		} else if ((game.getTeamHome().hasPlayer(defender) && game.getTurnDataHome().getApothecaries() > 0)
			|| (game.getTeamAway().hasPlayer(defender) && game.getTurnDataAway().getApothecaries() > 0)) {
			return TEAM;
		}

		return null;
	}

	public String getName() {
		return name;
	}
}
