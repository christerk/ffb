package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CLIENT_MODE)
@RulesCollection(Rules.COMMON)
public class ClientModeFactory implements INamedObjectFactory {

	public ClientMode forName(String pName) {
		for (ClientMode mode : ClientMode.values()) {
			if (mode.getName().equalsIgnoreCase(pName)) {
				return mode;
			}
		}
		return null;
	}

	public ClientMode forArgument(String pArgument) {
		for (ClientMode mode : ClientMode.values()) {
			if ((mode.getArgument() != null) && mode.getArgument().equalsIgnoreCase(pArgument)) {
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
