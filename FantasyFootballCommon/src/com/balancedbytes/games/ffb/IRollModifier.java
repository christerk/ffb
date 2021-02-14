package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.modifiers.ModifierKey;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifier<T extends ModifierKey> extends INamedObject {

	int getModifier();

	boolean isModifierIncluded();

	String getReportString();

	T getModifierKey();
}
