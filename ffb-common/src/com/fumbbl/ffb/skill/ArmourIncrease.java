package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;

public abstract class ArmourIncrease extends Skill {

	public ArmourIncrease() {
		super("+AV", SkillCategory.STAT_INCREASE);
	}

}
