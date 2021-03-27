package com.balancedbytes.games.ffb.modifiers;

public class StaticArmourModifier extends RegistrationAwareModifier implements ArmorModifier {

	private final String fName;
	private final int fModifier;
	private final boolean fFoulAssistModifier;

	public StaticArmourModifier(String pName, int pModifier, boolean pFoulAssistModifier) {
		fName = pName;
		fModifier = pModifier;
		fFoulAssistModifier = pFoulAssistModifier;
	}

	public int getModifier() {
		return fModifier;
	}

	public String getName() {
		return fName;
	}

	public boolean isFoulAssistModifier() {
		return fFoulAssistModifier;
	}

	public boolean appliesToContext(ArmorModifierContext context) {
		return true;
	}
}
