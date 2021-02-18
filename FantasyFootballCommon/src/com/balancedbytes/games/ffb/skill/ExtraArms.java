package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.PickupModifiers;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.ModifierDictionary;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;

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
	public void postConstruct(ModifierDictionary dictionary) {
		registerModifier(PickupModifiers.EXTRA_ARMS);
		registerModifier(new InterceptionModifier("Extra Arms", -1, false, false, dictionary));
		registerModifier(new CatchModifier("Extra Arms", -1, false, false, dictionary));
	}

}
