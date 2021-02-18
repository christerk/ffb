package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.model.ModifierDictionary;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class CatchModifier implements IRollModifier<CatchContext> {

	private final String name, reportingString;
	private final int fModifier;
	private final boolean fTacklezoneModifier;
	private final boolean fDisturbingPresenceModifier;

	public CatchModifier(String pName, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier, ModifierDictionary dictionary) {
		this(pName, pName, pModifier, pTacklezoneModifier, pDisturbingPresenceModifier, dictionary);
	}

	public CatchModifier(String name, String reportingString, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier, ModifierDictionary dictionary) {
		this.name = name;
		this.reportingString = reportingString;
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

	public boolean appliesToContext(Skill skill, CatchContext context) {
		return true;
	}

	@Override
	public String getReportString() {
		return reportingString;
	}

}
