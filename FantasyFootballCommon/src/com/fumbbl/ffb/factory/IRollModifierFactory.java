package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.modifiers.RollModifier;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifierFactory<T extends RollModifier<?>> extends INamedObjectFactory<T> {

	RollModifier<?> forName(String pName);

}
