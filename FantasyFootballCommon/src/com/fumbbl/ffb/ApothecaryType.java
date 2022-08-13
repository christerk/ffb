package com.fumbbl.ffb;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.ZappedPlayer;

import java.util.ArrayList;
import java.util.List;

public enum ApothecaryType {
	TEAM("Team Apothecary"), WANDERING("Wandering Apothecary"), PLAGUE("Plague Doctor");

	private final String name;

	ApothecaryType(String name) {
		this.name = name;
	}

	public static List<ApothecaryType> forPlayer(Game game, Player<?> defender, PlayerState playerState) {
		List<ApothecaryType> validTypes = new ArrayList<>();
		if (defender instanceof ZappedPlayer || defender.getPlayerType() == PlayerType.STAR) {
			return validTypes;
		}

		TurnData turnData = game.getTeamHome().hasPlayer(defender) ? game.getTurnDataHome() : game.getTurnDataAway();

		boolean teamHasWanderingApo = turnData.getWanderingApothecaries() > 0;

		boolean teamCanUsePlagueDoctor = turnData.getPlagueDoctors() > 0 && playerState.getBase() == PlayerState.KNOCKED_OUT;

		if (defender.getPlayerType() == PlayerType.MERCENARY) {
			if (teamHasWanderingApo) {
				validTypes.add(WANDERING);
			}
		} else if (defender.isJourneyman()) {

			if (teamHasWanderingApo) {
				validTypes.add(WANDERING);
			}

			if (teamCanUsePlagueDoctor) {
				validTypes.add(PLAGUE);
			}
		} else {
			if (turnData.getApothecaries() > turnData.getWanderingApothecaries()) {
				validTypes.add(TEAM);
			} else if (teamHasWanderingApo) {
				validTypes.add(WANDERING);
			}

			if (teamCanUsePlagueDoctor) {
				validTypes.add(PLAGUE);
			}
		}

		return validTypes;
	}

	public String getName() {
		return name;
	}
}
