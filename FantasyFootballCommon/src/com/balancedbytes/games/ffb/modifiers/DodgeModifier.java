package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.Skill;

/**
 *
 * @author Kalimar
 */
public class DodgeModifier implements IRollModifier<DodgeContext> {

	private final String fName;
	private final int fModifier;
	private final ModifierType type;
	private final boolean useStrength;

	public DodgeModifier(String pName, int pModifier, ModifierType type) {
		this(pName, pModifier, type, false);
	}

	public DodgeModifier(String pName, int pModifier, ModifierType type, boolean useStrength) {
		fName = pName;
		fModifier = pModifier;
		this.type = type;
		this.useStrength = useStrength;
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
		return type == ModifierType.TACKLEZONE || type == ModifierType.PREHENSILE_TAIL;
	}

	@Override
	public String getReportString() {
		return getName();
	}

	public boolean isUseStrength() {
		return useStrength;
	}
}
