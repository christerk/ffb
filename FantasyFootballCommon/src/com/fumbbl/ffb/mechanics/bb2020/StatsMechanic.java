package com.fumbbl.ffb.mechanics.bb2020;

import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.StatsDrawingModifier;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.modifiers.PlayerStatLimit;
import com.fumbbl.ffb.modifiers.TemporaryStatModifier;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StatsMechanic extends com.fumbbl.ffb.mechanics.StatsMechanic {
	@Override
	public boolean drawPassing() {
		return true;
	}

	@Override
	public String statSuffix() {
		return "+";
	}

	@Override
	public boolean armourIsBroken(int armour, int[] roll, InjuryContext context, Game game) {
		return (reduceArmour(context, armour, 8) <= (roll[0] + roll[1] + context.getArmorModifierTotal(game)));
	}

	@Override
	public StatsDrawingModifier agilityModifier(int modifier) {
		return StatsDrawingModifier.positiveImpairs(modifier);
	}

	@Override
	public int applyAgilityDecreases(int agility, int decreases) {
		return agility + decreases;
	}

	@Override
	public PlayerStatLimit limit(TemporaryStatModifier.PlayerStatKey key) {
		switch (key) {
			case MA:
				return new PlayerStatLimit(1, 9);
			case ST:
				return new PlayerStatLimit(1, 8);
			case AG:
				return new PlayerStatLimit(1, 6);
			case PA:
				return new PlayerStatLimit(1, 6);
			case AV:
				return new PlayerStatLimit(3, 11);
			default:
				return new PlayerStatLimit(0, 0);
		}
	}
}
