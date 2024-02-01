package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per game, Eldril may re-roll a failed Agility test when attempting to use the Hynoptic Gaze trait
 */

@RulesCollection(Rules.BB2020)
public class MesmerizingDance extends Skill {

	public MesmerizingDance() {
		super("Mesmerizing Dance", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerRerollSource(ReRolledActions.HYPNOTIC_GAZE, ReRollSources.MESMERIZING_DANCE);
	}
}
