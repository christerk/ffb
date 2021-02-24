package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.modifiers.RollModifier;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifierFactory<T extends RollModifier<?>> extends INamedObjectFactory<T> {

	RollModifier<?> forName(String pName);

}
