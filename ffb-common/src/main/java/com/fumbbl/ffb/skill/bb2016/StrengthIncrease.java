package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Player;

@RulesCollection(Rules.BB2016)
public class StrengthIncrease extends com.fumbbl.ffb.skill.StrengthIncrease {

	@Override
	public int getCost(Player<?> player) {
		return 50000;
	}

}
