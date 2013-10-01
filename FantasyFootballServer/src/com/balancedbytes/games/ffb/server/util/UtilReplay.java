package com.balancedbytes.games.ffb.server.util;

import java.nio.channels.SocketChannel;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerReplay;

/**
 * 
 * @author Kalimar
 */
public class UtilReplay {
	
	public static void startServerReplay(GameState pGameState, int pReplayToCommandNr, SocketChannel pSender) {
		if ((pGameState == null) || (pSender == null)) {
			return;
		}
    FantasyFootballServer server = pGameState.getServer();
    if (server.getChannelManager().getGameIdForChannel(pSender) != pGameState.getId()) {
    	server.getChannelManager().addChannel(pSender, pGameState, null, ClientMode.REPLAY, false);
      server.getCommunication().sendGameState(pSender, pGameState);
    }
    int replayToCommandNr = (pReplayToCommandNr > 0) ? pReplayToCommandNr : pGameState.lastCommandNr() + 1;
    server.getReplayer().add(new ServerReplay(pGameState, replayToCommandNr, pSender));
	}

}
