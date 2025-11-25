package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * This player is not Sent-off when performing a Foul Action if a natural 
 * double is rolled for the Armour Roll, so long as the target player’s 
 * Armour is not broken. If the target player’s Armour is broken, this 
 * player will be sent off as normal.
 */
@RulesCollection(Rules.BB2025)
public class SneakyGit extends Skill {

	public SneakyGit() {
		super("Sneaky Git", SkillCategory.DEVIOUS);
	}

}