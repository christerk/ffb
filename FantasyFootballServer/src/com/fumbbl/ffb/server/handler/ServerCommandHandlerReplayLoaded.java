package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandReplayLoaded;
import com.fumbbl.ffb.server.util.UtilServerReplay;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerReplayLoaded extends ServerCommandHandler {

	protected ServerCommandHandlerReplayLoaded(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_REPLAY_LOADED;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {

		InternalServerCommandReplayLoaded replayCommand = (InternalServerCommandReplayLoaded) pReceivedCommand.getCommand();

		if (replayCommand.getGameId() > 0) {
			GameState gameState = getServer().getGameCache().getGameStateById(replayCommand.getGameId());
			if (gameState != null) {
				gameState.setStatus(GameStatus.REPLAYING);
				UtilServerReplay.startServerReplay(gameState, replayCommand.getReplayToCommandNr(),
						pReceivedCommand.getSession());
			} else {
				getServer().getCommunication().sendStatus(pReceivedCommand.getSession(), ServerStatus.ERROR_UNKNOWN_GAME_ID,
						null);
			}
		}

		return true;

	}

}
