package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.CatchModifiers;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifierKey;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * The player ignores modifiers for enemy tackle zones when he attempts to pass,
 * catch or intercept.
 */
@RulesCollection(Rules.COMMON)
public class NervesOfSteel extends Skill {

	public NervesOfSteel() {
		super("Nerves of Steel", SkillCategory.PASSING);
	}

	@Override
	public void postConstruct() {
		registerModifier(new PassModifier("Nerves of Steel", 0, false, false));
		registerModifierKey(InterceptionModifierKey.NERVES_OF_STEEL);
		registerModifier(CatchModifiers.NERVES_OF_STEEL);

		registerProperty(NamedProperties.ignoreTacklezonesWhenPassing);
		registerProperty(NamedProperties.ignoreTacklezonesWhenCatching);
	}

}
