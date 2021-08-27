package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;

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
