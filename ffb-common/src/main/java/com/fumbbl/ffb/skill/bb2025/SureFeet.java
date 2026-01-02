package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * The player may re-roll the D6 if he is Knocked Down when trying to rush.
 * A player may only use the Sure Feet skill once per turn.
 */
@RulesCollection(Rules.BB2025)
public class SureFeet extends Skill {

	public SureFeet() {
		super("Sure Feet", SkillCategory.AGILITY, SkillUsageType.ONCE_PER_TURN);
	}

	@Override
	public void postConstruct() {
		registerRerollSource(ReRolledActions.RUSH, ReRollSources.SURE_FEET);
	}

}
