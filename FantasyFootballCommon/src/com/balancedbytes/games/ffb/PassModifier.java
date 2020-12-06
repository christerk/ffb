package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.PassingModifiers.PassContext;

/**
 * 
 * @author Kalimar
 */
public class PassModifier implements IRollModifier {
	private String fName;
	private int fModifier;
	private boolean fTacklezoneModifier;
	private boolean fDisturbingPresenceModifier;

	public PassModifier(String pName, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
		fName = pName;
		fModifier = pModifier;
		fTacklezoneModifier = pTacklezoneModifier;
		fDisturbingPresenceModifier = pDisturbingPresenceModifier;
		if (pTacklezoneModifier) {
			PassingModifiers.tackleZoneModifiers.add(this);
		}
		if (pDisturbingPresenceModifier) {
			PassingModifiers.disturbingPresenceModifiers.add(this);
		}
	}

	public String getName() {
		return fName;
	}

	public int getModifier() {
		return fModifier;
	}

	public boolean isTacklezoneModifier() {
		return fTacklezoneModifier;
	}

	public boolean isDisturbingPresenceModifier() {
		return fDisturbingPresenceModifier;
	}

	public boolean isModifierIncluded() {
		return (isTacklezoneModifier() || isDisturbingPresenceModifier());
	}

	public boolean appliesToContext(PassContext context) {
		return true;
	}
}
