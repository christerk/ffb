package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.modifiers.IRollModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;

/**
 * 
 * @author Kalimar
 */
public enum RightStuffModifier implements IRollModifier {

	SWOOP("Swoop", -1, ModifierType.REGULAR), KTM_MEDIUM("Medium Kick", 1, ModifierType.REGULAR), KTM_LONG("Long Kick", 2, ModifierType.REGULAR),
	TACKLEZONES_1("1 Tacklezone", 1, ModifierType.TACKLEZONE), TACKLEZONES_2("2 Tacklezones", 2, ModifierType.TACKLEZONE),
	TACKLEZONES_3("3 Tacklezones", 3, ModifierType.TACKLEZONE), TACKLEZONES_4("4 Tacklezones", 4, ModifierType.TACKLEZONE),
	TACKLEZONES_5("5 Tacklezones", 5, ModifierType.TACKLEZONE), TACKLEZONES_6("6 Tacklezones", 6, ModifierType.TACKLEZONE),
	TACKLEZONES_7("7 Tacklezones", 7, ModifierType.TACKLEZONE), TACKLEZONES_8("8 Tacklezones", 8, ModifierType.TACKLEZONE);

	private final String fName;
	private final int fModifier;
	private final ModifierType type;

	RightStuffModifier(String pName, int pModifier, ModifierType type) {
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
