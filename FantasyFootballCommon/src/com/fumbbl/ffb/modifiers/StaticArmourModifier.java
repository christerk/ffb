package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Player;

public class StaticArmourModifier extends RegistrationAwareModifier implements ArmorModifier {

	private final String fName;
	private final int fModifier;
	private final boolean fFoulAssistModifier;
	private final boolean chainsaw;

	public StaticArmourModifier(String pName, int pModifier, boolean pFoulAssistModifier) {
		this(pName, pModifier, pFoulAssistModifier, false);
	}

	public StaticArmourModifier(String pName, int pModifier, boolean pFoulAssistModifier, boolean chainsaw) {
		fName = pName;
		fModifier = pModifier;
		fFoulAssistModifier = pFoulAssistModifier;
		this.chainsaw = chainsaw;
	}

	public int getModifier(Player<?> player) {
		return fModifier;
	}

	public String getName() {
		return fName;
	}

	public boolean isFoulAssistModifier() {
		return fFoulAssistModifier;
	}

	public boolean isChainsaw() {
		return chainsaw;
	}

	public boolean appliesToContext(ArmorModifierContext context) {
		return true;
	}
}
