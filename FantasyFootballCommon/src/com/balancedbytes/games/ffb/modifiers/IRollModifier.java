package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifier<C extends ModifierContext> extends INamedObject {

	int getModifier();

	boolean isModifierIncluded();

	String getReportString();

	default int getMultiplier() {
		return getModifier();
	}

	default boolean appliesToContext(Skill skill, C context) {
		return true;
	}

	ModifierType getType();
}
