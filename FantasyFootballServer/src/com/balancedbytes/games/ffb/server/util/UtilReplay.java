package com.balancedbytes.games.ffb.server.util;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerReplay;

/**
 * 
 * @author Kalimar
 */
public class UtilReplay {
	
	public static void startServerReplay(GameState pGameState, int pReplayToCommandNr, Session pSession) {
		if ((pGameState == null) || (pSession == null)) {
			return;
		}
    FantasyFootballServer server = pGameState.getServer();
    if (server.getSessionManager().getGameIdForSession(pSession) != pGameState.getId()) {
    	server.getSessionManager().addSession(pSession, pGameState, null, ClientMode.REPLAY, false);
      server.getCommunication().sendGameState(pSession, pGameState);
    }
    int replayToCommandNr = (pReplayToCommandNr > 0) ? pReplayToCommandNr : pGameState.lastCommandNr() + 1;
    server.getReplayer().add(new ServerReplay(pGameState, replayToCommandNr, pSession));
	}

}
