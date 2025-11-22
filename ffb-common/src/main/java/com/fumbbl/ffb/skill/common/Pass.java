package com.fumbbl.ffb.skill.common;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player with the Pass skill is allowed to re-roll the D6 if he throws an
 * inaccurate pass or fumbles.
 */
@RulesCollection(Rules.COMMON)
public class Pass extends Skill {

	public Pass() {
		super("Pass", SkillCategory.PASSING);
	}

	@Override
	public void postConstruct() {
		registerRerollSource(ReRolledActions.PASS, ReRollSources.PASS);
	}

}
