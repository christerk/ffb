package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.LeapModifiers.LeapContext;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.modifiers.IRollModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;

/**
 * 
 * @author Kalimar
 */
public class LeapModifier implements IRollModifier {

	// TODO: create factory for this

	private final String fName;
	private final int fModifier;
	private final ModifierType type;

	LeapModifier(String pName, int pModifier, ModifierType type) {
		fName = pName;
		fModifier = pModifier;
		this.type = type;
	}

	public int getModifier() {
		return fModifier;
	}

	public String getName() {
		return fName;
	}

	@Override
	public ModifierType getType() {
		return type;
	}

	public boolean isModifierIncluded() {
		return false;
	}

	@Override
	public String getReportString() {
		return getName();
	}

	public boolean appliesToContext(Skill skill, LeapContext context) {
		return true;
	}
}
