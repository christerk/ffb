package com.fumbbl.ffb.skill.stars.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
/**
* Once per game, when an opposition player with a Strength characteristic of 5 or more is Knocked Down as
* the result of a Block action performed by Grim, you may apply an additional +1 modifier
* to either the Armour roll or Injury roll. This modifier may be applied after the roll has been made.
*/

@RulesCollection(Rules.BB2020)
public class Slayer extends Skill {
	public Slayer() {
		super("Slayer", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}
}
