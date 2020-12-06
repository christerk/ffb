package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.DodgeModifiers.DodgeContext;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class DodgeModifier implements IRollModifier {

	private String fName;
	private int fModifier;
	private boolean fTacklezoneModifier;
	private boolean fPrehensileTailModifier;

	public DodgeModifier(String pName, int pModifier, boolean pTacklezoneModifier, boolean pPrehensileTailModifier) {
		fName = pName;
		fModifier = pModifier;
		fTacklezoneModifier = pTacklezoneModifier;
		fPrehensileTailModifier = pPrehensileTailModifier;
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

	public boolean isPrehensileTailModifier() {
		return fPrehensileTailModifier;
	}

	public boolean isModifierIncluded() {
		return (isTacklezoneModifier() || isPrehensileTailModifier());
	}

	public boolean appliesToContext(Skill skill, DodgeContext context) {
		return true;
	}

}
