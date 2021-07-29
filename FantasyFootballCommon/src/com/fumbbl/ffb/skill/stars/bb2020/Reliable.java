package com.fumbbl.ffb.skill.stars.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
/**
 *Akhorne may choose to re-roll the d6 when rolling for the Dauntless skill
*/

@RulesCollection(Rules.BB2020)
public class Reliable extends Skill {
	public Reliable() {
		super("Reliable", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_TURN);
		
	}
}
