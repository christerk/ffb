package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.StatsDrawingModifier;
import com.fumbbl.ffb.model.Game;

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
		return (armour < (roll[0] + roll[1] + context.getArmorModifierTotal(game)));
	}

	@Override
	public StatsDrawingModifier agilityModifier(int modifier) {
		return StatsDrawingModifier.positiveImproves(modifier);
	}

	@Override
	public int applyAgilityDecreases(int agility, int decreases) {
		return agility - decreases;
	}
}
