package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.ServerReplay;
import org.eclipse.jetty.websocket.api.Session;

/**
 * 
 * @author Kalimar
 */
public class UtilServerReplay {

	public static void startServerReplay(GameState pGameState, int pReplayToCommandNr, Session pSession) {
		if ((pGameState == null) || (pSession == null)) {
			return;
		}
		FantasyFootballServer server = pGameState.getServer();
		if (server.getSessionManager().getGameIdForSession(pSession) != pGameState.getId()) {
			server.getCommunication().sendGameState(pSession, pGameState);
		}
		server.getReplayer().add(new ServerReplay(pGameState, pReplayToCommandNr, pSession));
	}

}
