package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.UtilCards;

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
		return UtilCards.hasSkill(context.getAttacker(), registeredTo);
	}
}
