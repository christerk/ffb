package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.RollModifier;

/**
 * 
 * @author Kalimar
 */
public class GazeModifier extends RollModifier<GazeModifierContext> {



	private final String fName;
	private final int fModifier;
	private final ModifierType type;

	GazeModifier(String pName, int pModifier, ModifierType type) {
		fName = pName;
		fModifier = pModifier;
		this.type = type;
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
		return type == ModifierType.TACKLEZONE;
	}

	@Override
	public String getReportString() {
		return getName();
	}


}
