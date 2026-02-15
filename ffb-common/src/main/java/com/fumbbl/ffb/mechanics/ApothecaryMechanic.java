package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.List;

public abstract class ApothecaryMechanic implements Mechanic {

	@Override
	public Type getType() {
		return Type.APOTHECARY;
	}

	public abstract List<ApothecaryType> apothecaryTypes(Game game, Player<?> defender, PlayerState playerState);

}
