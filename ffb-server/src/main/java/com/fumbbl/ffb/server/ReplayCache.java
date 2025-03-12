package com.fumbbl.ffb.server;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.server.net.SessionManager;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;

public class ReplayCache {
	private final FantasyFootballServer server;
	private final Map<String, ReplayState> statesByName;

	public ReplayCache(FantasyFootballServer server) {
		this.server = server;
		statesByName = new HashMap<>();
	}

	public synchronized ReplayState replayState(String name) {
		return statesByName.get(name);
	}

	public synchronized void add(String name, ReplayState replayState) {
		ReplayState previousState = statesByName.putIfAbsent(name, replayState);

		StringBuilder log = new StringBuilder();

		if (server.getDebugLog().isLogging(IServerLogLevel.WARN)) {
		if (previousState == null) {
			log.append("ADD REPLAY ");
			log.append(name);
			log.append(" cache increases to ").append(statesByName.size()).append(" replays.");
		} else {
			log.append("REPLAY ");
			log.append(name);
			log.append(" ALREADY EXISTS");
		}

			server.getDebugLog().log(IServerLogLevel.WARN, 0, log.toString());
		}
		// remove dead games from cache if there are no connections to the session
		SessionManager sessionManager = getServer().getSessionManager();
		Long[] gameIds = fGameStateById.keySet().toArray(new Long[0]);
		for (Long gameId : gameIds) {
			GameStatus status = fGameStateById.get(gameId).getStatus();
			if ((gameId == null) || (gameId == gameState.getId()) || (status == GameStatus.LOADING)) {
				continue;
			}
			Session[] sessions = sessionManager.getSessionsForGameId(gameId);
			if ((sessions.length == 0)
				|| ((sessions.length == 1) && ((ClientMode.SPECTATOR == sessionManager.getModeForSession(sessions[0]))
				|| (status == GameStatus.BACKUPED)))) {
				closeGame(gameId);
			}
		}

	}
}
