package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPing;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerPing extends ServerCommandHandler {

	protected ServerCommandHandlerPing(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_PING;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {
		ClientCommandPing pingCommand = (ClientCommandPing) pReceivedCommand.getCommand();
		if (getServer().getReplaySessionManager().has(pReceivedCommand.getSession())) {
			getServer().getReplaySessionManager().setLastPing(pReceivedCommand.getSession(), System.currentTimeMillis());
		} else {
			getServer().getSessionManager().setLastPing(pReceivedCommand.getSession(), System.currentTimeMillis());
		}
		getServer().getCommunication().sendPong(pReceivedCommand.getSession(), pingCommand.getTimestamp());
		return true;
	}

}
