package com.fumbbl.ffb;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;
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

		TurnData turnData = game.getTeamHome().hasPlayer(defender) ? game.getTurnDataHome() : game.getTurnDataAway();

		boolean teamHasWanderingApo = turnData.getWanderingApothecaries() > 0;

		boolean teamHasPlagueDoctor = turnData.getPlagueDoctors() > 0;

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
		} else if (turnData.getApothecaries() > turnData.getWanderingApothecaries()) {
			return TEAM;
		} else if (teamHasWanderingApo) {
			return WANDERING;
		} else if (teamHasPlagueDoctor) {
			return PLAGUE;
		}

		return null;
	}

	public String getName() {
		return name;
	}
}
