package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.util.UtilCards;

public class StaticInjuryModifierDefender extends StaticInjuryModifier {
	public StaticInjuryModifierDefender(String pName, int pModifier, boolean pNigglingInjuryModifier) {
		super(pName, pModifier, pNigglingInjuryModifier);
	}

	@Override
	public boolean appliesToContext(InjuryModifierContext context) {
		return UtilCards.hasSkill(context.getDefender(), registeredTo);
	}
}
