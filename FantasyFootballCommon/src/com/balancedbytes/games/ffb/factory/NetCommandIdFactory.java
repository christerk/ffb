package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.NET_COMMAND_ID)
@RulesCollection(Rules.COMMON)
public class NetCommandIdFactory implements INamedObjectFactory {

	public NetCommandId forName(String pName) {
		for (NetCommandId commandId : NetCommandId.values()) {
			if (commandId.getName().equalsIgnoreCase(pName)) {
				return commandId;
			}
		}
		return null;
	}

	@Override
	public void initialize(GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
