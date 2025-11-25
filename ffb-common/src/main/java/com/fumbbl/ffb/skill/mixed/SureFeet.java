package com.fumbbl.ffb.skill.mixed;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * The player may re-roll the D6 if he is Knocked Down when trying to rush.
 * A player may only use the Sure Feet skill once per turn.
 */
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class SureFeet extends Skill {

	public SureFeet() {
		super("Sure Feet", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerRerollSource(ReRolledActions.RUSH, ReRollSources.SURE_FEET);
	}

}
