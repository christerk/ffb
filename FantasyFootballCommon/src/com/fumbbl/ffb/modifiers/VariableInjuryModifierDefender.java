package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilCards;

public class VariableInjuryModifierDefender extends VariableInjuryModifier {
	public VariableInjuryModifierDefender(String pName, boolean pNigglingInjuryModifier) {
		super(pName, pNigglingInjuryModifier);
	}

	@Override
	protected Player<?> relevantPlayer(Player<?> attacker, Player<?> defender) {
		return defender;
	}

	@Override
	public boolean appliesToContext(InjuryModifierContext context) {
		return UtilCards.hasSkill(context.getDefender(), registeredTo);
	}
}
