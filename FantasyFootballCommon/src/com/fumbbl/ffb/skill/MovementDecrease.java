package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(Rules.COMMON)
public class MovementDecrease extends Skill {

	public MovementDecrease() {
		super("-MA", SkillCategory.STAT_DECREASE);
	}

}
