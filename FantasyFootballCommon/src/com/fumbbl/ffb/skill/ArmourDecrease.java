package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ArmourDecrease extends Skill {

	public ArmourDecrease() {
		super("-AV", SkillCategory.STAT_DECREASE);
	}
}
