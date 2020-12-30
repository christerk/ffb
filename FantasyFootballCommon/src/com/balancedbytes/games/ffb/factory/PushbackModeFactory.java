package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PUSHBACK_MODE)
@RulesCollection(Rules.COMMON)
public class PushbackModeFactory implements INamedObjectFactory {

	public PushbackMode forName(String pName) {
		for (PushbackMode mode : PushbackMode.values()) {
			if (mode.getName().equalsIgnoreCase(pName)) {
				return mode;
			}
		}
		return null;
	}

	@Override
	public void initialize(GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
