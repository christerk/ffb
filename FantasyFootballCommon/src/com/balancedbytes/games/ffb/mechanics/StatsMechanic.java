package com.balancedbytes.games.ffb.mechanics;

import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.model.Game;

public abstract class StatsMechanic implements Mechanic {

	@Override
	public Type getType() {
		return Type.STAT;
	}

	public abstract boolean drawPassing();

	public abstract String statSuffix();

	public abstract boolean armourIsBroken(int armour, int[] roll, InjuryContext context, Game game);

	public abstract StatsDrawingModifier agilityModifier(int modifier);
}
