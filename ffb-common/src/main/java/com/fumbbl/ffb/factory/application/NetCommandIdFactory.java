package com.fumbbl.ffb.factory.application;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommandId;

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
	public void initialize(Game game) {
	}

}
