package com.fumbbl.ffb.skill.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Player;

@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class ArmourIncrease extends com.fumbbl.ffb.skill.ArmourIncrease {

	@Override
	public int getCost(Player<?> player) {
		return 10000;
	}

}
