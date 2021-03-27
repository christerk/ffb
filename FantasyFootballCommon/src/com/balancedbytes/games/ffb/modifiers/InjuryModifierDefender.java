package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.util.UtilCards;

public class InjuryModifierDefender extends StaticInjuryModifier {
	public InjuryModifierDefender(String pName, int pModifier, boolean pNigglingInjuryModifier) {
		super(pName, pModifier, pNigglingInjuryModifier);
	}

	@Override
	public boolean appliesToContext(InjuryModifierContext context) {
		return UtilCards.hasSkill(context.getDefender(), registeredTo);
	}
}
