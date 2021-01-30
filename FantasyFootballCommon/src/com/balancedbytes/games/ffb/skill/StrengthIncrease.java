package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

public abstract class StrengthIncrease extends Skill {

	public StrengthIncrease() {
		super("+ST", SkillCategory.STAT_INCREASE);
	}
}
