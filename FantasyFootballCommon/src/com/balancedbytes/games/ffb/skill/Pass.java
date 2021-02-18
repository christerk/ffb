package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.ReRollSources;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.ModifierDictionary;
import com.balancedbytes.games.ffb.model.Skill;

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
	public void postConstruct(ModifierDictionary dictionary) {
		registerRerollSource(ReRolledActions.PASS, ReRollSources.PASS);
	}

}
