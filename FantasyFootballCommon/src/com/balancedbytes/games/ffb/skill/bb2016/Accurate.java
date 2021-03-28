package com.balancedbytes.games.ffb.skill.bb2016;

import com.balancedbytes.games.ffb.modifiers.ModifierType;
import com.balancedbytes.games.ffb.modifiers.PassModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.skill.Skill;

/**
 * The player may add 1 to the D6 roll when he passes.
 */
@RulesCollection(Rules.BB2016)
public class Accurate extends Skill {

	public Accurate() {
		super("Accurate", SkillCategory.PASSING);
	}

	@Override
	public void postConstruct(){
		registerModifier(new PassModifier("Accurate", -1, ModifierType.REGULAR));
	}

}
