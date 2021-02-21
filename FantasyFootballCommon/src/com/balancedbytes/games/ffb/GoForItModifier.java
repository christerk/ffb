package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.modifiers.IRollModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;

/**
 * 
 * @author Kalimar
 */
public enum GoForItModifier implements IRollModifier {

	BLIZZARD("Blizzard", 1), GREASED_SHOES("Greased Shoes", 3);

	private final String fName;
	private final int fModifier;
	private final ModifierType type;

	GoForItModifier(String pName, int pModifier) {
		fName = pName;
		fModifier = pModifier;
		type = ModifierType.REGULAR;
	}

	@Override
	public ModifierType getType() {
		return type;
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

}
