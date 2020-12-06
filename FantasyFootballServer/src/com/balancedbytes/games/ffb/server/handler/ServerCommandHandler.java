package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;

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
