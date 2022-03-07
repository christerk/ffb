package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per game, Helmut may use his Pro skill to re-roll a single dice rolled as part of an Armour roll
 */

@RulesCollection(Rules.BB2020)
public class OldPro extends Skill {

	public OldPro() {
		super("Old Pro", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}
}
