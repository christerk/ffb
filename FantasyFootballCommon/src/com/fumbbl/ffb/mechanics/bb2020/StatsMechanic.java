package com.fumbbl.ffb.mechanics.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.mechanics.StatsDrawingModifier;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.modifiers.PlayerStatKey;
import com.fumbbl.ffb.modifiers.PlayerStatLimit;

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
	public int applyInGameAgilityInjury(int agility, int decreases) {
		return agility + decreases;
	}

	@Override
	public PlayerStatLimit limit(PlayerStatKey key) {
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

	@Override
	public int applyLastingInjury(int startingValue, PlayerStatKey key) {
		PlayerStatLimit limit = limit(key);

		switch (key) {
			case AG:
			case PA:
				return Math.min(startingValue + 1, limit.getMax());
			default:
				return Math.max(startingValue - 1, limit.getMin());
		}
	}
}
