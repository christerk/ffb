package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class InjuryModifier extends RegistrationAwareModifier {

	private final String fName;
	private final int fModifier;
	private final boolean fNigglingInjuryModifier;

	public InjuryModifier(String pName, int pModifier, boolean pNigglingInjuryModifier) {
		fName = pName;
		fModifier = pModifier;
		fNigglingInjuryModifier = pNigglingInjuryModifier;
	}

	public int getModifier() {
		return fModifier;
	}

	public String getName() {
		return fName;
	}

	public boolean isNigglingInjuryModifier() {
		return fNigglingInjuryModifier;
	}

	public boolean appliesToContext(Skill skill, InjuryContext context) {
		return true;
	}

	public boolean appliesToContext(InjuryModifierContext context) {
		return true;
	}

}
