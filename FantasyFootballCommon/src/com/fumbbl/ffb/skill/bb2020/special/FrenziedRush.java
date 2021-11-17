package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
/**
*Once per game, when Glart performs a Blitz Action, he may gain the Frenzy skill.
* You must declare this special rule is being used when Glart is activated. 
* Glart may not use the Grab skill during a turn in which he uses this special rule.

*/

@RulesCollection(Rules.BB2020)
public class FrenziedRush extends Skill {
	public FrenziedRush() {
		super("FrenziedRush", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}
}
