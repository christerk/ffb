package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
/**
* Once per game, when Wilhelm makes an injury roll against an opposing player, he may choose to re-roll the result

*/

@RulesCollection(Rules.BB2020)
public class SavageMauling extends Skill {
	public SavageMauling() {
		super("SavageMauling", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}
}
