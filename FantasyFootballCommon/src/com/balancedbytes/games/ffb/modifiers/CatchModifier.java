package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class CatchModifier implements IRollModifier<CatchContext> {

	private final String name;
	private final int fModifier;
	private final boolean fTacklezoneModifier;
	private final boolean fDisturbingPresenceModifier;

	public CatchModifier(String name, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
		this.name = name;
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

	public boolean appliesToContext(Skill skill, CatchContext context) {
		return true;
	}

	@Override
	public String getReportString() {
		return getName();
	}

}
