package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerCloseSession extends ServerCommandHandler {

	protected ServerCommandHandlerCloseSession(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_CLOSE_SESSION;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {
		getServer().getCommunication().close(pReceivedCommand.getSession());
		return true;
	}

}
