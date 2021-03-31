package com.balancedbytes.games.ffb.modifiers;

/**
 *
 * @author Kalimar
 */
public class DodgeModifier extends RollModifier<DodgeContext> {

	private final String fName, reportString;
	private final int fModifier, multiplier;
	private final ModifierType type;
	private final boolean useStrength;

	public DodgeModifier(String pName, int pModifier, ModifierType type) {
		this(pName, pModifier, type, false);
	}

	public DodgeModifier(String pName, int pModifier, ModifierType type, boolean useStrength) {
		this(pName, pName, pModifier, type, useStrength);
	}

	public DodgeModifier(String pName, String reportString, int pModifier, ModifierType type, boolean useStrength) {
		this(pName, reportString, pModifier, pModifier, type, useStrength);
	}

	public DodgeModifier(String pName, String reportString, int pModifier, int multiplier, ModifierType type, boolean useStrength) {
		fName = pName;
		this.reportString = reportString;
		fModifier = pModifier;
		this.type = type;
		this.useStrength = useStrength;
		this.multiplier = multiplier;
	}

	@Override
	public ModifierType getType() {
		return type;
	}

	public int getModifier() {
		return fModifier;
	}

	@Override
	public int getMultiplier() {
		return multiplier;
	}

	public String getName() {
		return fName;
	}

	public boolean isModifierIncluded() {
		return type == ModifierType.TACKLEZONE || type == ModifierType.PREHENSILE_TAIL;
	}

	@Override
	public String getReportString() {
		return reportString;
	}

	public boolean isUseStrength() {
		return useStrength;
	}
}
