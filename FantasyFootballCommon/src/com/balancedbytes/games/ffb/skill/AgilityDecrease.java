package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

@RulesCollection(RulesCollection.Rules.COMMON)
public class AgilityDecrease extends Skill {

	public AgilityDecrease() {
		super("-AG", SkillCategory.STAT_DECREASE);
	}
}
