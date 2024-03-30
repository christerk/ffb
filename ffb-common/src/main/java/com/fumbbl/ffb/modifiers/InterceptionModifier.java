package com.fumbbl.ffb.modifiers;

public class InterceptionModifier extends RollModifier<InterceptionContext> {
	private final String name;
	private final int fModifier, multiplier;
	private final String reportString;
	private final ModifierType type;

	public InterceptionModifier(String name, int pModifier, ModifierType type) {
		this(name, name, pModifier, pModifier, type);
	}

	public InterceptionModifier(String name, String reportString, int pModifier, ModifierType type) {
		this(name, reportString, pModifier, pModifier, type);
	}
	public InterceptionModifier(String name, String reportString, int pModifier, int multiplier, ModifierType type) {
		this.reportString = reportString;
		this.multiplier = multiplier;
		this.name = name;
		fModifier = pModifier;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public int getModifier() {
		return fModifier;
	}

	@Override
	public ModifierType getType() {
		return type;
	}

	public boolean isModifierIncluded() {
		return type == ModifierType.TACKLEZONE || type == ModifierType.DISTURBING_PRESENCE;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public String getReportString() {
		return reportString;
	}
}
