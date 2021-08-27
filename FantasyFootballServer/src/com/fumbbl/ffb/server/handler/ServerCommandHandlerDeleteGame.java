package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandDeleteGame;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerDeleteGame extends ServerCommandHandler {

	protected ServerCommandHandlerDeleteGame(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_DELETE_GAME;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {
		InternalServerCommandDeleteGame deleteGameCommand = (InternalServerCommandDeleteGame) pReceivedCommand.getCommand();
		getServer().getGameCache().queueDbDelete(deleteGameCommand.getGameId(), deleteGameCommand.isWithGamesInfo());
		getServer().getDebugLog().log(IServerLogLevel.WARN, deleteGameCommand.getGameId(), "GameState deleted from db");
		return true;
	}

}
