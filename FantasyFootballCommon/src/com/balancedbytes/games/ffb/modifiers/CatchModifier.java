package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.RollModifier;

/**
 * 
 * @author Kalimar
 */
public class CatchModifier extends RollModifier<CatchContext> {

	private final String name, reportingString;
	private final int fModifier;
	private final boolean fTacklezoneModifier;
	private final boolean fDisturbingPresenceModifier;

	public CatchModifier(String pName, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
		this(pName, pName, pModifier, pTacklezoneModifier, pDisturbingPresenceModifier);
	}

	public CatchModifier(String name, String reportingString, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
		this.name = name;
		this.reportingString = reportingString;
		fModifier = pModifier;
		fTacklezoneModifier = pTacklezoneModifier;
		fDisturbingPresenceModifier = pDisturbingPresenceModifier;
	}

	public String getName() {
		return name;
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

	public boolean appliesToContext(CatchContext context) {
		return true;
	}

	@Override
	public String getReportString() {
		return reportingString;
	}
}
