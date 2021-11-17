package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
/**
*Once per game, after making a Passing Ability test to perform a Pass action, Skrull may choose to modify the dice roll by adding his Strength characteristic to it

*/

@RulesCollection(Rules.BB2020)
public class StrongPassingGame extends Skill {
	public StrongPassingGame() {
		super("StrongPassingGame", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}
}
