package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Player;

@RulesCollection(Rules.BB2020)
public class StrengthIncrease extends com.fumbbl.ffb.skill.StrengthIncrease {

	@Override
	public int getCost(Player<?> player) {
		return 80000;
	}

}
