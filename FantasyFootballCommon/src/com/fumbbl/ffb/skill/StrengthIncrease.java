package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;

public abstract class StrengthIncrease extends Skill {

	public StrengthIncrease() {
		super("+ST", SkillCategory.STAT_INCREASE);
	}
}
