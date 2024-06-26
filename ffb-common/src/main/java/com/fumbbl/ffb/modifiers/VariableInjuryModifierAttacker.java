package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilCards;

public class VariableInjuryModifierAttacker extends VariableInjuryModifier {
	public VariableInjuryModifierAttacker(String pName, boolean pNigglingInjuryModifier) {
		super(pName, pNigglingInjuryModifier);
	}

	@Override
	protected Player<?> relevantPlayer(Player<?> attacker, Player<?> defender) {
		return attacker;
	}

	@Override
	public boolean appliesToContext(InjuryModifierContext context) {
		return context.isAttackerMode() && UtilCards.hasSkill(context.getAttacker(), registeredTo);
	}
}
