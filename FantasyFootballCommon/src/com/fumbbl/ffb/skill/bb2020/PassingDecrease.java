package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(Rules.BB2020)
public class PassingDecrease extends Skill {

	public PassingDecrease() {
		super("-PA", SkillCategory.STAT_DECREASE);
	}

}
