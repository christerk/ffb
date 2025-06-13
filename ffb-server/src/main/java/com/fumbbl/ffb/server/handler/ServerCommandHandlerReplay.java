package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandReplay;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.request.ServerRequestLoadReplay;
import com.fumbbl.ffb.server.util.UtilServerReplay;
import org.eclipse.jetty.websocket.api.Session;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerReplay extends ServerCommandHandler {

	protected ServerCommandHandlerReplay(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_REPLAY;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {

		ClientCommandReplay replayCommand = (ClientCommandReplay) pReceivedCommand.getCommand();
		Session session = pReceivedCommand.getSession();
		int replayToCommandNr = replayCommand.getReplayToCommandNr();

		long gameId = replayCommand.getGameId();

		if (gameId == 0) {
			SessionManager sessionManager = getServer().getSessionManager();
			gameId = sessionManager.getGameIdForSession(pReceivedCommand.getSession());
		}

		if (gameId == 0) {
			return false;
		}

		GameState gameState = getServer().getGameCache().getGameStateById(gameId);
		if (gameState == null) {
			gameState = getServer().getGameCache().queryFromDb(gameId);
			getServer().getGameCache().addGame(gameState);
		}

		if (gameState != null) {
			UtilServerReplay.startServerReplay(gameState, replayToCommandNr, pReceivedCommand.getSession());

		} else {
			// game has been moved out of the db - request it from the backup service
			getServer().getRequestProcessor().add(new ServerRequestLoadReplay(replayCommand.getGameId(), replayToCommandNr,
				session, ServerRequestLoadReplay.LOAD_GAME, null, replayCommand.getCoach()));
		}

		return true;

	}

}
