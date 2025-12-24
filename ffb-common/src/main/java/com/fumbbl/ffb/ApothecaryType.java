package com.fumbbl.ffb;

import com.fumbbl.ffb.mechanics.ApothecaryMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.List;

public enum ApothecaryType {
	TEAM("Team Apothecary"), WANDERING("Wandering Apothecary"), PLAGUE("Plague Doctor");

	private final String name;

	ApothecaryType(String name) {
		this.name = name;
	}

	public static List<ApothecaryType> forPlayer(Game game, Player<?> defender, PlayerState playerState) {
		ApothecaryMechanic apothecaryMechanic = (ApothecaryMechanic) game.getMechanic(Mechanic.Type.APOTHECARY);

		return apothecaryMechanic.apothecaryTypes(game, defender, playerState);
	}

	public String getName() {
		return name;
	}
}
