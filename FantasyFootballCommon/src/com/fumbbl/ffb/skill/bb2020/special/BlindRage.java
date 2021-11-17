package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
/**
 *Akhorne may choose to re-roll the d6 when rolling for the Dauntless skill
*/

@RulesCollection(Rules.BB2020)
public class BlindRage extends Skill {
	public BlindRage() {
		super("BlindRage", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
		
		registerRerollSource(ReRolledActions.DAUNTLESS, ReRollSources.BLIND_RAGE_RE_ROLL);
	}
}
