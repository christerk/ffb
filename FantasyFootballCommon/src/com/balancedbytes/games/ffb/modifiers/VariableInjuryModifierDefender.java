package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.UtilCards;

public class VariableInjuryModifierDefender extends VariableInjuryModifier {
	public VariableInjuryModifierDefender(String pName, boolean pNigglingInjuryModifier) {
		super(pName, pNigglingInjuryModifier);
	}

	@Override
	protected Player<?> relevantPlayer(InjuryModifierContext context) {
		return context.getDefender();
	}

	@Override
	public boolean appliesToContext(InjuryModifierContext context) {
		return UtilCards.hasSkill(context.getDefender(), registeredTo);
	}
}
