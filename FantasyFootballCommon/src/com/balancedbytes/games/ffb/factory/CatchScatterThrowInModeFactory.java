package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.catchScatterThrowInMode)
public class CatchScatterThrowInModeFactory implements INamedObjectFactory {

	public CatchScatterThrowInMode forName(String pName) {
		for (CatchScatterThrowInMode mode : CatchScatterThrowInMode.values()) {
			if (mode.getName().equalsIgnoreCase(pName)) {
				return mode;
			}
		}
		return null;
	}

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
