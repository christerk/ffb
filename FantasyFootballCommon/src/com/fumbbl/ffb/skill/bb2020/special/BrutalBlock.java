package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
/**
*Once per game, when Frank 'n' Stein makes an Injury roll against an opponent as a result of a Block action,
* he may choose to add an additional +1 modifier to the injury roll. this modifier may be applied after the roll has been made

*/

@RulesCollection(Rules.BB2020)
public class BrutalBlock extends Skill {
	public BrutalBlock() {
		super("BrutalBlock", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}
}
