package com.balancedbytes.games.ffb.skill.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;

@RulesCollection(Rules.BB2016)
public class ArmourIncrease extends com.balancedbytes.games.ffb.skill.ArmourIncrease {

	@Override
	public int getCost(Player<?> player) {
		return 30000;
	}

}