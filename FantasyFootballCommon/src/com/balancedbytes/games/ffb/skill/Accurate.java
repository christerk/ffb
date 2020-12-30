package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.PassingModifiers;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * The player may add 1 to the D6 roll when he passes.
 */
@RulesCollection(Rules.COMMON)
public class Accurate extends Skill {

	public Accurate() {
		super("Accurate", SkillCategory.PASSING);
	}

	@Override
	public void postConstruct(){
		registerModifier(PassingModifiers.ACCURATE);
	}

}
