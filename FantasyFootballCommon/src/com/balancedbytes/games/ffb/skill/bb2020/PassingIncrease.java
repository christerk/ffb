package com.balancedbytes.games.ffb.skill.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.skill.Skill;

@RulesCollection(Rules.BB2020)
public class PassingIncrease extends Skill {

	public PassingIncrease() {
		super("+PA", SkillCategory.STAT_INCREASE);
	}

	@Override
	public int getCost(Player<?> player) {
		return 30000;
	}
}
