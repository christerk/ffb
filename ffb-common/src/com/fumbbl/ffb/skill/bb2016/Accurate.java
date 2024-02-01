package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.PassModifier;

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
