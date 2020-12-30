package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;

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
