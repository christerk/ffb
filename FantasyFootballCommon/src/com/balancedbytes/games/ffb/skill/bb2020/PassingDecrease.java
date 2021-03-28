package com.balancedbytes.games.ffb.skill.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.skill.Skill;

@RulesCollection(Rules.BB2020)
public class PassingDecrease extends Skill {

	public PassingDecrease() {
		super("-PA", SkillCategory.STAT_DECREASE);
	}

}
