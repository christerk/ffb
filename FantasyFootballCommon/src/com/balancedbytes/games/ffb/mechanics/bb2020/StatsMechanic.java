package com.balancedbytes.games.ffb.mechanics.bb2020;

import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.RulesCollection;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StatsMechanic extends com.balancedbytes.games.ffb.mechanics.StatsMechanic {
	@Override
	public boolean drawPassing() {
		return true;
	}

	@Override
	public String statSuffix() {
		return "+";
	}

	@Override
	public boolean armourIsBroken(int armour, int[] roll, InjuryContext context) {
		return (armour <= (roll[0] + roll[1] + context.getArmorModifierTotal()));
	}
}
