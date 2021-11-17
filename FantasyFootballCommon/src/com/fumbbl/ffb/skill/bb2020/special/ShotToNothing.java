package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per game, when Gloriel performs a Pass Action,
 * she may gain the Hail Mary Pass skill. You must declare this special rule is being used when Gloriel is activated
 */

@RulesCollection(Rules.BB2020)
public class ShotToNothing extends Skill {
	public ShotToNothing() {
		super("Shot to Nothing", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}
}
