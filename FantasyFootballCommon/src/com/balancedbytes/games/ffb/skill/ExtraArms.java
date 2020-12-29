package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.CatchModifiers;
import com.balancedbytes.games.ffb.InterceptionModifiers;
import com.balancedbytes.games.ffb.PickupModifiers;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * A player with one or more extra arms may add 1 to any attempt to pick up,
 * catch or intercept.
 */
@RulesCollection(Rules.All)
public class ExtraArms extends Skill {

	public ExtraArms() {
		super("Extra Arms", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerModifier(PickupModifiers.EXTRA_ARMS);
		registerModifier(InterceptionModifiers.EXTRA_ARMS);
		registerModifier(CatchModifiers.EXTRA_ARMS);
	}

}
