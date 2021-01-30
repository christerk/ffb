package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

public abstract class ArmourIncrease extends Skill {

	public ArmourIncrease() {
		super("+AV", SkillCategory.STAT_INCREASE);
	}

}
