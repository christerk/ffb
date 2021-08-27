package com.fumbbl.ffb.modifiers;

/**
 * 
 * @author Kalimar
 */
public class PassModifier extends RollModifier<PassContext> {
	private final String fName, reportingString;
	private final int fModifier;
	private final ModifierType type;

	public PassModifier(String pName, int pModifier, ModifierType type) {
		this(pName, pName, pModifier, type);
	}

	public PassModifier(String pName, String reportingString, int pModifier, ModifierType type) {
		fName = pName;
		this.reportingString = reportingString;
		fModifier = pModifier;
		this.type = type;
	}

	@Override
	public ModifierType getType() {
		return type;
	}

	public String getName() {
		return fName;
	}

	public int getModifier() {
		return fModifier;
	}

	public boolean isModifierIncluded() {
		return type == ModifierType.TACKLEZONE || type == ModifierType.DISTURBING_PRESENCE;
	}

	@Override
	public String getReportString() {
		return reportingString;
	}

}
