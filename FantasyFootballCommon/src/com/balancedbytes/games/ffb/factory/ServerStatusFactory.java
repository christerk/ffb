package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.net.ServerStatus;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.serverStatus)
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
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
