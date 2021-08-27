package com.fumbbl.ffb.server.net.commands;

import com.fumbbl.ffb.net.NetCommandId;

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
