package com.fumbbl.ffb.modifiers;

/**
 * @author Kalimar
 */
public class GazeModifier extends RollModifier<GazeModifierContext> {

	private final String fName, reportString;
	private final int fModifier, multiplier;
	private final ModifierType type;

	public GazeModifier(String pName, int pModifier, ModifierType type) {
		this(pName, pName, pModifier, pModifier, type);
	}

	public GazeModifier(String pName, String reportString, int pModifier, int multiplier, ModifierType type) {
		fName = pName;
		this.reportString = reportString;
		fModifier = pModifier;
		this.type = type;
		this.multiplier = multiplier;
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
		return reportString;
	}

	@Override
	public int getMultiplier() {
		return multiplier;
	}
}
