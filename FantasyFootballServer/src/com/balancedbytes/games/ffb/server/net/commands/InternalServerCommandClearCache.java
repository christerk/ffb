package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandClearCache extends InternalServerCommand {

	public InternalServerCommandClearCache() {
		super();
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_CLEAR_CACHE;
	}

}
