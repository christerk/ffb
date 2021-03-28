package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ArmourDecrease extends Skill {

	public ArmourDecrease() {
		super("-AV", SkillCategory.STAT_DECREASE);
	}
}
