package com.fumbbl.ffb.skill.stars.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
/**
*Once per game, when Zolcath is activated, he may gain the Hypnotic Gaze trait. 
* You must declare this special rule is being used when Zolcath is activated

*/

@RulesCollection(Rules.BB2020)
public class ExcuseMeAreYouAZoat extends Skill {
	public ExcuseMeAreYouAZoat() {
		super("ExcuseMeAreYouAZoat", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}
}
