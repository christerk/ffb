package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
/**
*Once per game, after making an agility test to dodge, Gretchen may choose to modify the dice roll by adding her Strength characteristic to it.

*/

@RulesCollection(Rules.BB2020)
public class Incorpreal extends Skill {
	public Incorpreal() {
		super("Incorpreal", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}
}
