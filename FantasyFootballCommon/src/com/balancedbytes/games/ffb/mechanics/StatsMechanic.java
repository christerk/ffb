package com.balancedbytes.games.ffb.mechanics;

import com.balancedbytes.games.ffb.InjuryContext;

public abstract class StatsMechanic implements Mechanic {

	@Override
	public Type getType() {
		return Type.STAT;
	}

	public abstract boolean drawPassing();

	public abstract String statSuffix();

	public abstract boolean armourIsBroken(int armour, int[] roll, InjuryContext context);

	public abstract StatsDrawingModifier agilityModifier(int modifier);
}
