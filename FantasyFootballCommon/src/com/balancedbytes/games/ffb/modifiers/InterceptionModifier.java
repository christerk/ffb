package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.RollModifier;
import com.balancedbytes.games.ffb.model.ModifierDictionary;

public class InterceptionModifier extends RollModifier<InterceptionContext> {
	private final String name;
	private final int fModifier, multiplier;
	private final boolean fTacklezoneModifier;
	private final boolean fDisturbingPresenceModifier;
	private final String reportString;

	public InterceptionModifier(String name, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier, ModifierDictionary dictionary) {
		this(name, name, pModifier, pModifier, pTacklezoneModifier, pDisturbingPresenceModifier, dictionary);
	}

	public InterceptionModifier(String name, String reportString, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier, ModifierDictionary dictionary) {
		this(name, reportString, pModifier, pModifier, pTacklezoneModifier, pDisturbingPresenceModifier, dictionary);
	}
	public InterceptionModifier(String name, String reportString, int pModifier, int multiplier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier, ModifierDictionary dictionary) {
		this.reportString = reportString;
		this.multiplier = multiplier;
		this.name = name;
		fModifier = pModifier;
		fTacklezoneModifier = pTacklezoneModifier;
		fDisturbingPresenceModifier = pDisturbingPresenceModifier;
		dictionary.add(this);
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

	public boolean appliesToContext(InterceptionContext context) {
		return true;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public String getReportString() {
		return reportString;
	}
}
