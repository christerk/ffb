package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per game, Eldril may re-roll the dice when performing a Hypnotic Gaze Special Action.
 */

@RulesCollection(Rules.BB2025)
public class MesmerisingDance extends Skill {

	public MesmerisingDance() {
		super("Mesmerising Dance", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_HALF);
	}

	@Override
	public void postConstruct() {
		registerRerollSource(ReRolledActions.HYPNOTIC_GAZE, ReRollSources.MESMERISING_DANCE);
	}
}
