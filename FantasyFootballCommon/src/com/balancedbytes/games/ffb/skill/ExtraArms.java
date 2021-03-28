package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;
import com.balancedbytes.games.ffb.modifiers.PickupModifier;

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
