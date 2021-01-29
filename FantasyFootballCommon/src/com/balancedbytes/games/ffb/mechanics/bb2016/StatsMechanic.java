package com.balancedbytes.games.ffb.mechanics.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;

@RulesCollection(RulesCollection.Rules.BB2016)
public class StatsMechanic extends com.balancedbytes.games.ffb.mechanics.StatsMechanic {
	@Override
	public boolean drawPassing() {
		return false;
	}

	@Override
	public String statSuffix() {
		return "";
	}
}
