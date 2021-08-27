package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CLIENT_STATE_ID)
@RulesCollection(Rules.COMMON)
public class ClientStateIdFactory implements INamedObjectFactory {

	public ClientStateId forName(String pName) {
		for (ClientStateId state : ClientStateId.values()) {
			if (state.getName().equalsIgnoreCase(pName)) {
				return state;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
