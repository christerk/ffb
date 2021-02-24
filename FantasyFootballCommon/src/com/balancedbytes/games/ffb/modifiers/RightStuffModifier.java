package com.balancedbytes.games.ffb.modifiers;

/**
 * 
 * @author Kalimar
 */
public class RightStuffModifier implements IRollModifier<RightStuffContext> {

	private final String fName;
	private final int fModifier;
	private final ModifierType type;

	public RightStuffModifier(String pName, int pModifier, ModifierType type) {
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
