package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.INamedObject;

/**
 * 
 * @author Kalimar
 */
public class StaticInjuryModifier extends RegistrationAwareModifier implements INamedObject, InjuryModifier {

	private final String fName;
	private final int fModifier;
	private final boolean fNigglingInjuryModifier;

	public StaticInjuryModifier(String pName, int pModifier, boolean pNigglingInjuryModifier) {
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

	public boolean appliesToContext(InjuryModifierContext context) {
		return true;
	}

}
