package com.balancedbytes.games.ffb.modifiers;

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

	public boolean appliesToContext(InjuryModifierContext context) {
		return true;
	}

}
