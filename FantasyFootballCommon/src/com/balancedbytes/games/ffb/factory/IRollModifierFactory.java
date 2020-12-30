package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.IRollModifier;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifierFactory<T> extends INamedObjectFactory<T> {

	public IRollModifier forName(String pName);

}
