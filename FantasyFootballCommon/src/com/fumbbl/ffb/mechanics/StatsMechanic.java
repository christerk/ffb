package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.model.Game;

public abstract class StatsMechanic implements Mechanic {

	@Override
	public Type getType() {
		return Type.STAT;
	}

	public abstract boolean drawPassing();

	public abstract String statSuffix();

	public abstract boolean armourIsBroken(int armour, int[] roll, InjuryContext context, Game game);

	public abstract StatsDrawingModifier agilityModifier(int modifier);

	public abstract int applyAgilityDecreases(int agility, int decreases);
}
