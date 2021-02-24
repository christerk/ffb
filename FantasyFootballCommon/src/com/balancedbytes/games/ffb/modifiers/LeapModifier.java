package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.Skill;

/**
 *
 * @author Kalimar
 */
public class LeapModifier implements IRollModifier<LeapContext> {

	// TODO: create factory for this

	private final String fName;
	private final int fModifier;
	private final ModifierType type;

	public LeapModifier(String pName, int pModifier, ModifierType type) {
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
