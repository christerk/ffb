package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Player;

public class VariableArmourModifier extends RegistrationAwareModifier implements ArmorModifier {

	private final String fName;
	private final boolean fFoulAssistModifier;

	public VariableArmourModifier(String pName, boolean pFoulAssistModifier) {
		fName = pName;
		fFoulAssistModifier = pFoulAssistModifier;
	}

	public int getModifier(Player<?> attacker, Player<?> defender) {
		return attacker.getSkillIntValue(registeredTo);
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
