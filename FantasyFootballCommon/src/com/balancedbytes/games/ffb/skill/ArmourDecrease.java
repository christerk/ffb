package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

public abstract class ArmourDecrease extends Skill {

	public ArmourDecrease() {
		super("-AV", SkillCategory.STAT_DECREASE);
	}
}
