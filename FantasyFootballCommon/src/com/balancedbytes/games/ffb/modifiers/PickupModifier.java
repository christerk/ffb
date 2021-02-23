package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class PickupModifier implements IRollModifier<PickupContext> {
	private final String fName, reportString;
	private final int fModifier;
	private final ModifierType type;

	public PickupModifier(String pName, int pModifier, ModifierType type) {
		this(pName, pName, pModifier, type);
	}

	public PickupModifier(String pName, String reportString, int pModifier, ModifierType type) {
		fName = pName;
		fModifier = pModifier;
		this.type = type;
		this.reportString = reportString;
	}

	public int getModifier() {
		return fModifier;
	}

	public String getName() {
		return fName;
	}

	@Override
	public ModifierType getType() {
		return type;
	}

	public boolean isModifierIncluded() {
		return type == ModifierType.TACKLEZONE;
	}

	public boolean appliesToContext(Skill skill, PickupContext context) {
		return true;
	}

	@Override
	public String getReportString() {
		return reportString;
	}

}
