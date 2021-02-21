package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.PickupModifiers.PickupContext;
import com.balancedbytes.games.ffb.modifiers.IRollModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;

/**
 * 
 * @author Kalimar
 */
public class PickupModifier implements IRollModifier {
	private final String fName;
	private final int fModifier;
	private final ModifierType type;

	public PickupModifier(String pName, int pModifier, ModifierType type) {
		fName = pName;
		fModifier = pModifier;
		this.type = type;
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

	public boolean appliesToContext(PickupContext context) {
		return true;
	}

	@Override
	public String getReportString() {
		return getName();
	}

}
