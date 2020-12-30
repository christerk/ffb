package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.ServerStatus;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.SERVER_STATUS)
@RulesCollection(Rules.COMMON)
public class ServerStatusFactory implements INamedObjectFactory {

	public ServerStatus forName(String pName) {
		for (ServerStatus serverStatus : ServerStatus.values()) {
			if (serverStatus.getName().equalsIgnoreCase(pName)) {
				return serverStatus;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
