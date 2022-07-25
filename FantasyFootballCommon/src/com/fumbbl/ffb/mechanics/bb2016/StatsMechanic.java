package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.mechanics.StatsDrawingModifier;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.modifiers.PlayerStatKey;
import com.fumbbl.ffb.modifiers.PlayerStatLimit;

@RulesCollection(RulesCollection.Rules.BB2016)
public class StatsMechanic extends com.fumbbl.ffb.mechanics.StatsMechanic {
	@Override
	public boolean drawPassing() {
		return false;
	}

	@Override
	public String statSuffix() {
		return "";
	}

	@Override
	public boolean armourIsBroken(int armour, int[] roll, InjuryContext context, Game game) {
		return (reduceArmour(context, armour, 7) < (roll[0] + roll[1] + context.getArmorModifierTotal(game)));
	}

	@Override
	public StatsDrawingModifier agilityModifier(int modifier) {
		return StatsDrawingModifier.positiveImproves(modifier);
	}

	@Override
	public int applyInGameAgilityInjury(int agility, int decreases) {
		return agility - decreases;
	}

	@Override
	public PlayerStatLimit limit(PlayerStatKey key) {
		switch (key) {
			case MA:
			case ST:
			case AG:
			case AV:
				return new PlayerStatLimit(1, 10);
			default:
				return new PlayerStatLimit(0, 0);
		}
	}

	@Override
	public int applyLastingInjury(int startingValue, PlayerStatKey key) {
		PlayerStatLimit limit = limit(key);

		return Math.max(startingValue - 1, limit.getMin());
	}

	@Override
	public boolean statCanBeReducedByInjury(int originalValue, int currentValue) {
		return (originalValue - currentValue) < 2;
	}
}
