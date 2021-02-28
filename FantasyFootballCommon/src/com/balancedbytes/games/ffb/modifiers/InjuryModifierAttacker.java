package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.util.UtilCards;

public class InjuryModifierAttacker extends InjuryModifier {
	public InjuryModifierAttacker(String pName, int pModifier, boolean pNigglingInjuryModifier) {
		super(pName, pModifier, pNigglingInjuryModifier);
	}

	@Override
	public boolean appliesToContext(InjuryModifierContext context) {
		return UtilCards.hasSkill(context.getGame(), context.getAttacker(), registeredTo);
	}
}
