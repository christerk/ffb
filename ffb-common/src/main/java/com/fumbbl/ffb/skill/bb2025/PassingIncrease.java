package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(Rules.BB2025)
public class PassingIncrease extends Skill {

	public PassingIncrease() {
		super("+PA", SkillCategory.STAT_INCREASE);
	}

	@Override
	public int getCost(Player<?> player) {
		return 20000;
	}
}
