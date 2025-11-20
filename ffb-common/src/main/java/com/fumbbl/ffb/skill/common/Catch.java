package com.fumbbl.ffb.skill.common;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player who has the Catch skill is allowed to re-roll the D6 if he fails a
 * catch roll. It also allows the player to re-roll the D6 if he drops a
 * hand-off or fails to make an interception.
 */
@RulesCollection(Rules.COMMON)
public class Catch extends Skill {

	public Catch() {
		super("Catch", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerRerollSource(ReRolledActions.CATCH, ReRollSources.CATCH);
	}

}
