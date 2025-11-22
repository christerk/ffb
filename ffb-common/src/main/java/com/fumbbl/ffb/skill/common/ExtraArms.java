package com.fumbbl.ffb.skill.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.PickupModifier;

/**
 * A player with one or more extra arms may add 1 to any attempt to pick up,
 * catch or intercept.
 */
@RulesCollection(Rules.COMMON)
public class ExtraArms extends Skill {

	public ExtraArms() {
		super("Extra Arms", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerModifier(new PickupModifier("Extra Arms", -1, ModifierType.REGULAR));
		registerModifier(new InterceptionModifier("Extra Arms", -1, ModifierType.REGULAR));
		registerModifier(new CatchModifier("Extra Arms", -1, ModifierType.REGULAR));
	}

}
