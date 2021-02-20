package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.ReRollSources;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

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
