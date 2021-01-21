package com.balancedbytes.games.ffb.mechanics;

import com.balancedbytes.games.ffb.model.Player;

public abstract class PassMechanic implements Mechanic {

	@Override
	public Type getType() {
		return Type.PASS;
	}

	public abstract boolean eligibleToPass(Player<?> player);

}
