package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.RollModifier;

/**
 * 
 * @author Kalimar
 */
public class GoForItModifier extends RollModifier<GoForItContext> {

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