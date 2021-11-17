package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
/**
*Once per game, Griff may re-roll one dice that was rolled either as a single dice roll,
* as port of a multiple dice roll or as poart of a dice pool (this cannot be a dice that was rolled 
* as part of an Armour, Injury or Casualty roll)

*/

@RulesCollection(Rules.BB2020)
public class ConsumateProfessional extends Skill {
	public ConsumateProfessional() {
		super("ConsumateProfessional", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}
}
