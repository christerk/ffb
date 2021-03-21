package com.balancedbytes.games.ffb.mechanics;

public abstract class JumpMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.JUMP;
	}
}
