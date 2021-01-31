package com.balancedbytes.games.ffb.skill.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Player;

@RulesCollection(Rules.BB2020)
public class StrengthIncrease extends com.balancedbytes.games.ffb.skill.StrengthIncrease {

	@Override
	public int getCost(Player<?> player) {
		return 80000;
	}

}
