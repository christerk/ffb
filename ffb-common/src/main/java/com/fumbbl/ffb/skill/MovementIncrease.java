package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(Rules.COMMON)
public class MovementIncrease extends Skill {

	public MovementIncrease() {
		super("+MA", SkillCategory.STAT_INCREASE);
	}

	@Override
	public int getCost(Player<?> player) {
		return 30000;
	}
}
