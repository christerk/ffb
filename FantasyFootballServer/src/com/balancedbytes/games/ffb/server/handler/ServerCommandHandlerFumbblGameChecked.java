package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandFumbblGameChecked;
import com.balancedbytes.games.ffb.server.util.UtilServerStartGame;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerFumbblGameChecked extends ServerCommandHandler {

	protected ServerCommandHandlerFumbblGameChecked(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_FUMBBL_GAME_CHECKED;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {
		InternalServerCommandFumbblGameChecked gameCheckedCommand = (InternalServerCommandFumbblGameChecked) pReceivedCommand
				.getCommand();
		GameState gameState = getServer().getGameCache().getGameStateById(gameCheckedCommand.getGameId());
		UtilServerStartGame.startGame(gameState);
		return true;
	}

}
