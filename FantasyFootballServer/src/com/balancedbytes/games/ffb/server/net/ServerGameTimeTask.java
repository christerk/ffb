package com.balancedbytes.games.ffb.server.net;

import java.util.TimerTask;

import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.server.util.UtilServerTimer;

public class ServerGameTimeTask extends TimerTask {

	private FantasyFootballServer fServer;

	public ServerGameTimeTask(FantasyFootballServer server) {
		fServer = server;
	}

	public void run() {
		try {
			long currentTimeMillis = System.currentTimeMillis();
			for (GameState gameState : fServer.getGameCache().allGameStates()) {
				Game game = gameState.getGame();
				boolean timeoutPossible = game.isTimeoutPossible();
				UtilServerTimer.syncTime(gameState, currentTimeMillis);
				fServer.getCommunication().sendGameTime(gameState);
				// check if timeout flag has changes -> sync game model
				if (timeoutPossible != game.isTimeoutPossible()) {
					UtilServerGame.syncGameModel(gameState, null, null, SoundId.WHISTLE);
				}
			}
		} catch (Exception anyException) {
			getServer().getDebugLog().log(anyException);
			System.exit(99);
		}
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

}
