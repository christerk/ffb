package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.modifiers.CatchContext;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class CatchModifier implements IRollModifier<CatchModifierKey> {

	private CatchModifierKey modifierKey;
	private int fModifier;
	private boolean fTacklezoneModifier;
	private boolean fDisturbingPresenceModifier;

	public CatchModifier(CatchModifierKey modifierKey, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
		this.modifierKey = modifierKey;
		fModifier = pModifier;
		fTacklezoneModifier = pTacklezoneModifier;
		fDisturbingPresenceModifier = pDisturbingPresenceModifier;
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

	public boolean appliesToContext(CatchContext context) {
		return true;
	}

	public boolean appliesToContext(Skill skill, CatchContext context) {
		return true;
	}

	public CatchModifierKey getModifierKey() {
		return modifierKey;
	}

	@Override
	public String getReportString() {
		return getName();
	}

}
