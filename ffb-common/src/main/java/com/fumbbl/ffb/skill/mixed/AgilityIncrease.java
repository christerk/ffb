package com.fumbbl.ffb.skill.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2016)
@RulesCollection(RulesCollection.Rules.BB2020)
public class AgilityIncrease extends Skill {

	public AgilityIncrease() {
		super("+AG", SkillCategory.STAT_INCREASE);
	}

	@Override
	public int getCost(Player<?> player) {
		return 40000;
	}
}
