package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(Rules.COMMON)
public class StrengthDecrease extends Skill {

	public StrengthDecrease() {
		super("-ST", SkillCategory.STAT_DECREASE);
	}

}
