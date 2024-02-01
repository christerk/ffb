package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.ServerReplay;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;

/**
 * 
 * @author Kalimar
 */
public class UtilServerReplay {

	public static void startServerReplay(GameState pGameState, int pReplayToCommandNr, Session pSession, String coach) {
		if ((pGameState == null) || (pSession == null)) {
			return;
		}
		FantasyFootballServer server = pGameState.getServer();
		if (server.getSessionManager().getGameIdForSession(pSession) != pGameState.getId()) {
			server.getSessionManager().addSession(pSession, pGameState.getId(), coach, ClientMode.REPLAY, false, new ArrayList<>());
			server.getCommunication().sendGameState(pSession, pGameState);
		}
		server.getReplayer().add(new ServerReplay(pGameState, pReplayToCommandNr, pSession));
	}

}
