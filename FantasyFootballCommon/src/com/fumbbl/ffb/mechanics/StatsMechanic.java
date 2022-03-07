package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.PlayerStatKey;
import com.fumbbl.ffb.modifiers.PlayerStatLimit;

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

	public abstract int applyInGameAgilityInjury(int agility, int decreases);

	protected int reduceArmour(InjuryContext context, int armour, int reductionValue) {
		if ((armour > reductionValue) &&
			Arrays.stream(context.getArmorModifiers())
				.anyMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.reducesArmourToFixedValue))) {
			return reductionValue;
		}
		return armour;
	}

	public abstract PlayerStatLimit limit(PlayerStatKey key);

	public abstract int applyLastingInjury(int startingValue, PlayerStatKey key);
}
