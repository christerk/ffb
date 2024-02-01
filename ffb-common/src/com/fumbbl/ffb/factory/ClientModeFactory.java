package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

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
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
