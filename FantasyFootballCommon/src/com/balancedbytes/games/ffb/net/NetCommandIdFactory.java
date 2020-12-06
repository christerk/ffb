package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.INamedObjectFactory;

/**
 * 
 * @author Kalimar
 */
public class NetCommandIdFactory implements INamedObjectFactory {

	public NetCommandId forName(String pName) {
		for (NetCommandId commandId : NetCommandId.values()) {
			if (commandId.getName().equalsIgnoreCase(pName)) {
				return commandId;
			}
		}
		return null;
	}

}
