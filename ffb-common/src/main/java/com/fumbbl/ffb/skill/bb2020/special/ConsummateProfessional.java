package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per game, Griff may apply a +1 modifier to an Agility Test he has made.
 * This modifier may be applied after the roll has been made.
 */

@RulesCollection(Rules.BB2020)
public class ConsummateProfessional extends Skill {
	public ConsummateProfessional() {
		super("Consummate Professional", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {

	}
}
