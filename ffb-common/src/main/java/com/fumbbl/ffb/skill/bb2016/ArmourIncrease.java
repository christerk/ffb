package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Player;

@RulesCollection(Rules.BB2016)
public class ArmourIncrease extends com.fumbbl.ffb.skill.ArmourIncrease {

	@Override
	public int getCost(Player<?> player) {
		return 30000;
	}

}
