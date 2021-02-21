package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.RollModifier;

/**
 * 
 * @author Kalimar
 */
public class CatchModifier extends RollModifier<CatchContext> {

	private final String name, reportingString;
	private final int fModifier;
	private final ModifierType type;

	public CatchModifier(String pName, int pModifier, ModifierType type) {
		this(pName, pName, pModifier, type);
	}

	public CatchModifier(String name, String reportingString, int pModifier, ModifierType type) {
		this.name = name;
		this.reportingString = reportingString;
		fModifier = pModifier;
		this.type = type;
	}

	@Override
	public ModifierType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public int getModifier() {
		return fModifier;
	}

	public boolean isModifierIncluded() {
		return type == ModifierType.DISTURBING_PRESENCE || type == ModifierType.TACKLEZONE;
	}

	@Override
	public String getReportString() {
		return reportingString;
	}
}
