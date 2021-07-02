package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;

import java.util.Arrays;

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

	protected int reduceArmour(InjuryContext context, int armour, int reductionValue) {
		if ((armour > reductionValue) &&
			Arrays.stream(context.getArmorModifiers())
				.anyMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.reducesArmourToFixedValue))) {
			return reductionValue;
		}
		return armour;
	}
}
