package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.ServerStatus;

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
