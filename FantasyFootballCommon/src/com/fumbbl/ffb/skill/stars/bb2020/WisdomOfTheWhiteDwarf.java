package com.fumbbl.ffb.skill.stars.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
/**
* Once per team turn, when on of Grombrindal's team-mates that is in an adjacent square
*  is activated, that player gains either the Break Tackle, Dauntless, Mighty Blow (+1) or Sure 
*  Feet skill until the end of their activation
*/

@RulesCollection(Rules.BB2020)
public class WisdomOfTheWhiteDwarf extends Skill {
	public WisdomOfTheWhiteDwarf() {
		super("WisdomOfTheWhiteDwarf", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_TURN);
	}
}
