package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per game, when Skitter successfully breaks an opposition player's armour
 * as a result of a Stab Special Action, he may choose to re-roll the Injury roll.
 */

@RulesCollection(Rules.BB2025)
public class MasterAssassin extends Skill {
	public MasterAssassin() {
		super("Master Assassin", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}
}
