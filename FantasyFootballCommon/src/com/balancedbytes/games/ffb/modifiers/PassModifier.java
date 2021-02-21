package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.IRollModifier;

/**
 * 
 * @author Kalimar
 */
public class PassModifier implements IRollModifier<PassContext> {
	private final String fName, reportingString;
	private final int fModifier;
	private final boolean fTacklezoneModifier;
	private final boolean fDisturbingPresenceModifier;

	public PassModifier(String pName, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
		this(pName, pName, pModifier, pTacklezoneModifier, pDisturbingPresenceModifier);
	}

	public PassModifier(String pName, String reportingString, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
		fName = pName;
		this.reportingString = reportingString;
		fModifier = pModifier;
		fTacklezoneModifier = pTacklezoneModifier;
		fDisturbingPresenceModifier = pDisturbingPresenceModifier;
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

	@Override
	public String getReportString() {
		return reportingString;
	}

}
