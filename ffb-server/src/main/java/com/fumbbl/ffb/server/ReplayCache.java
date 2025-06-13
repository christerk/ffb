package com.fumbbl.ffb.server;

import com.fumbbl.ffb.server.net.ReplaySessionManager;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
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

	public synchronized void add(ReplayState replayState) {
		String name = replayState.getName();
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

			server.getDebugLog().logReplay(IServerLogLevel.WARN, name, null, log.toString());
		}
		// remove dead games from cache if there are no connections to the session
		ReplaySessionManager sessionManager = server.getReplaySessionManager();

		for (String replayName : statesByName.keySet()) {
			if ((replayName == null) || (replayName.equals(name))) {
				continue;
			}
			Session[] sessions = sessionManager.sessionsForReplay(replayName);
			if (!ArrayTool.isProvided(sessions)) {
				closeReplay(replayName);
			}
		}
	}

	public void closeReplay(String replayName) {
		if (!StringTool.isProvided(replayName)) {
			return;
		}
		ReplayState replayState = replayState(replayName);
		if (replayState != null) {
			removeReplay(replayName);
		}
	}

	private void removeReplay(String replayName) {
		ReplayState cachedState = statesByName.remove(replayName);
		if (cachedState != null) {
			// log game 	cache size
			server.getDebugLog().logReplay(IServerLogLevel.WARN, replayName, null,
				StringTool.bind("REMOVE REPLAY $1 cache decreases to $2 games.", replayName, statesByName.size()));
		}
	}
}
