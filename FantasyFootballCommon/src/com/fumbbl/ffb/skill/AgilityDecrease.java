package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.COMMON)
public class AgilityDecrease extends Skill {

	public AgilityDecrease() {
		super("-AG", SkillCategory.STAT_DECREASE);
	}
}
