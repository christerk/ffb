package com.fumbbl.ffb.server.net.commands;

import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandSocketClosed extends InternalServerCommand {

	public InternalServerCommandSocketClosed() {
		super();
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_SOCKET_CLOSED;
	}

}
