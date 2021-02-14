package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.LeapModifiers.LeapContext;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.modifiers.ModifierKey;

/**
 * 
 * @author Kalimar
 */
public class LeapModifier implements IRollModifier {

	// TODO: create factory for this

	private String fName;
	private int fModifier;

	LeapModifier(String pName, int pModifier) {
		fName = pName;
		fModifier = pModifier;
	}

	public int getModifier() {
		return fModifier;
	}

	public String getName() {
		return fName;
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

	@Override
	public ModifierKey getModifierKey() {
		return null;
	}
}
