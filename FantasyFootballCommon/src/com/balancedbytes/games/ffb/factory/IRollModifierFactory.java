package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.IRollModifier;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifierFactory extends INamedObjectFactory {

	public IRollModifier forName(String pName);

}
