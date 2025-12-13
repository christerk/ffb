package com.fumbbl.ffb.skill.mixed.special;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * Akhorne may choose to re-roll the d6 when rolling for the Dauntless skill
 */

@RulesCollection(Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class BlindRage extends Skill {
	public BlindRage() {
		super("Blind Rage", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerRerollSource(ReRolledActions.DAUNTLESS, ReRollSources.BLIND_RAGE);
	}
}
