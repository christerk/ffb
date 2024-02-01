package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandCloseGame;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerCloseGame extends ServerCommandHandler {

	protected ServerCommandHandlerCloseGame(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_CLOSE_GAME;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {
		InternalServerCommandCloseGame closeGameCommand = (InternalServerCommandCloseGame) pReceivedCommand.getCommand();
		getServer().getGameCache().closeGame(closeGameCommand.getGameId());
		return true;
	}

}
