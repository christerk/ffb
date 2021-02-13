package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class InterceptionModifier implements IRollModifier {
	private final InterceptionModifierKey modifierKey;
	private final int fModifier, multiplier;
	private final boolean fTacklezoneModifier;
	private final boolean fDisturbingPresenceModifier;
	private final String reportString;

	public InterceptionModifier(InterceptionModifierKey modifierKey, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
		this(modifierKey, modifierKey.getName(), pModifier, pModifier, pTacklezoneModifier, pDisturbingPresenceModifier);
	}

	public InterceptionModifier(InterceptionModifierKey modifierKey, String reportString, int pModifier, int multiplier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
		this.reportString = reportString;
		this.multiplier = multiplier;
		this.modifierKey = modifierKey;
		fModifier = pModifier;
		fTacklezoneModifier = pTacklezoneModifier;
		fDisturbingPresenceModifier = pDisturbingPresenceModifier;
	}

	public InterceptionModifierKey getModifierKey() {
		return modifierKey;
	}

	public String getName() {
		return modifierKey.getName();
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

	public boolean appliesToContext(Skill skill, InterceptionContext context) {
		return true;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public String getReportString() {
		return reportString;
	}
}
