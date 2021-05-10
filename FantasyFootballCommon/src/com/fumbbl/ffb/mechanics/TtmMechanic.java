package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public abstract class TtmMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.TTM;
	}

	public abstract Player<?>[] findThrowableTeamMates(Game pGame, Player<?> pThrower);

	public abstract boolean canBeThrown(Game game, Player<?> player);
}
