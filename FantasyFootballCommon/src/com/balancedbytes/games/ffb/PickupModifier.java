package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.PickupModifiers.PickupContext;

/**
 * 
 * @author Kalimar
 */
public class PickupModifier implements IRollModifier {
	private String fName;
	private int fModifier;
	private boolean fTacklezoneModifier;

	public PickupModifier(String pName, int pModifier, boolean pTacklezoneModifier) {
		fName = pName;
		fModifier = pModifier;
		fTacklezoneModifier = pTacklezoneModifier;
	}

	public int getModifier() {
		return fModifier;
	}

	public String getName() {
		return fName;
	}

	public boolean isTacklezoneModifier() {
		return fTacklezoneModifier;
	}

	public boolean isModifierIncluded() {
		return isTacklezoneModifier();
	}

	public boolean appliesToContext(PickupContext context) {
		return true;
	}

	@Override
	public String getReportString() {
		return getName();
	}
}
