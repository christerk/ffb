package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
public abstract class VariableInjuryModifier extends RegistrationAwareModifier implements INamedObject, InjuryModifier {

	private final String fName;
	private final boolean fNigglingInjuryModifier;

	public VariableInjuryModifier(String pName, boolean pNigglingInjuryModifier) {
		fName = pName;
		fNigglingInjuryModifier = pNigglingInjuryModifier;
	}

	protected abstract Player<?> relevantPlayer(Player<?> attacker, Player<?> defender);

	public int getModifier(Player<?> attacker, Player<?> defender) {
		return relevantPlayer(attacker, defender).getSkillIntValue(registeredTo);
	}

	public String getName() {
		return fName;
	}

	public boolean isNigglingInjuryModifier() {
		return fNigglingInjuryModifier;
	}

	public boolean appliesToContext(InjuryModifierContext context) {
		return true;
	}

}
