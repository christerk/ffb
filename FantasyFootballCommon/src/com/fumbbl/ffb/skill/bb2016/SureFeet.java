package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * The player may re-roll the D6 if he is Knocked Down when trying to Go For It
 * (see page 20). A player may only use the Sure Feet skill once per turn.
 */
@RulesCollection(Rules.BB2016)
public class SureFeet extends Skill {

	public SureFeet() {
		super("Sure Feet", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerRerollSource(ReRolledActions.GO_FOR_IT, ReRollSources.SURE_FEET);
	}

}
