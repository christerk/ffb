package com.fumbbl.ffb.modifiers;

/**
 *
 * @author Kalimar
 */
public class DodgeModifier extends RollModifier<DodgeContext> {

	private final String fName, reportString;
	private final int fModifier, multiplier;
	private final ModifierType type;
	private final boolean useStrength;
	private final boolean optional;

	public DodgeModifier(String pName, int pModifier, ModifierType type) {
		this(pName, pModifier, type, false);
	}

	public DodgeModifier(String pName, int pModifier, ModifierType type, boolean useStrength) {
		this(pName, pName, pModifier, type, useStrength);
	}

	public DodgeModifier(String pName, int pModifier, ModifierType type, boolean useStrength, boolean optional) {
		this(pName, pName, pModifier, pModifier, type, useStrength, optional);
	}

	public DodgeModifier(String pName, String reportString, int pModifier, ModifierType type, boolean useStrength) {
		this(pName, reportString, pModifier, pModifier, type, useStrength);
	}

	public DodgeModifier(String pName, String reportString, int pModifier, ModifierType type, boolean useStrength,
											 boolean optional) {
		this(pName, reportString, pModifier, pModifier, type, useStrength, optional);
	}

	public DodgeModifier(String pName, String reportString, int pModifier, int multiplier, ModifierType type, boolean useStrength) {
		this(pName, reportString, pModifier, multiplier, type, useStrength, false);
	}

	public DodgeModifier(String pName, String reportString, int pModifier, int multiplier, ModifierType type,
											 boolean useStrength, boolean optional) {
		fName = pName;
		this.reportString = reportString;
		fModifier = pModifier;
		this.type = type;
		this.useStrength = useStrength;
		this.multiplier = multiplier;
		this.optional = optional;
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

	/**
	 * Optional modifiers are never applied automatically, they only apply when the coach explicitly selects the
	 * skill providing them.
	 */
	public boolean isOptional() {
		return optional;
	}
}
