package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.util.UtilCards;

public class StaticInjuryModifierAttacker extends StaticInjuryModifier {
	public StaticInjuryModifierAttacker(String pName, int pModifier, boolean pNigglingInjuryModifier) {
		super(pName, pModifier, pNigglingInjuryModifier);
	}

	@Override
	public boolean appliesToContext(InjuryModifierContext context) {
		return UtilCards.hasSkill(context.getAttacker(), registeredTo);
	}
}
