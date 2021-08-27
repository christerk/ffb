package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.FantasyFootballServer;

/**
 * 
 * @author Kalimar
 */
public abstract class ServerCommandHandler implements IReceivedCommandHandler {

	private FantasyFootballServer fServer;

	protected ServerCommandHandler(FantasyFootballServer pServer) {
		fServer = pServer;
	}

	public abstract NetCommandId getId();

	protected FantasyFootballServer getServer() {
		return fServer;
	}

}
