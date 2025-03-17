package com.fumbbl.ffb.server.net;

import com.fumbbl.ffb.marking.AutoMarkingConfig;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class ReplaySessionManager {

	private final Map<String, Set<Session>> sessionsForReplay;
	private final Map<Session, ReplayClient> replayClientForSession;
	private final Map<Session, Long> lastPingBySession;
	private final Map<Session, AutoMarkingConfig> autoMarkingBySession;

	public ReplaySessionManager() {
		sessionsForReplay = new HashMap<>();
		replayClientForSession = new HashMap<>();
		lastPingBySession = new HashMap<>();
		autoMarkingBySession = new HashMap<>();
	}

	public synchronized void addSession(Session pSession, String name, String pCoach) {
		ReplayClient client = new ReplayClient(name, pCoach);
		replayClientForSession.put(pSession, client);
		Set<Session> sessions = sessionsForReplay.computeIfAbsent(name, k -> new HashSet<>());
		sessions.add(pSession);
		lastPingBySession.put(pSession, System.currentTimeMillis());
	}

	public synchronized void removeSession(Session session) {
		String name = getSharedReplayName(session);
		Set<Session> sessions = sessionsForReplay.get(name);
		if (sessions != null) {
			sessions.remove(session);
			if (sessions.isEmpty()) {
				sessionsForReplay.remove(name);
			}
		}
		lastPingBySession.remove(session);
		replayClientForSession.remove(session);
		autoMarkingBySession.remove(session);
	}

	private String getSharedReplayName(Session pSession) {
		ReplayClient replayClient = replayClientForSession.get(pSession);
		if (replayClient == null) {
			return "";
		}
		return replayClient.sharedReplayName;
	}

	public synchronized Session[] sessionsForReplay(String replayName) {
		return sessionsForReplay.get(replayName).toArray(new Session[0]);
	}

	public synchronized String coach(Session session) {
		ReplayClient replayClient = replayClientForSession.get(session);
		if (replayClient == null) {
			return null;
		}
		return replayClient.coach;
	}

	public synchronized String replayNameForSession(Session session) {
		ReplayClient replayClient = replayClientForSession.get(session);
		if (replayClient == null) {
			return "";
		}

		return replayClient.sharedReplayName;
	}

	public synchronized Set<Session> otherSessions(Session session) {
		String replayName = replayNameForSession(session);

		if (!StringTool.isProvided(replayName)) {
			return Collections.emptySet();
		}

		return Arrays.stream(sessionsForReplay(replayName)).filter(other -> other != session).collect(Collectors.toSet());
	}

	public synchronized boolean has(Session session) {
		return replayClientForSession.containsKey(session);
	}

	public synchronized void addAutoMarking(Session session, AutoMarkingConfig autoMarkingConfig) {
		autoMarkingBySession.put(session, autoMarkingConfig);
	}

	public synchronized AutoMarkingConfig getAutoMarking(Session session) {
		return autoMarkingBySession.get(session);
	}

	public synchronized void setLastPing(Session pSession, long pPing) {
		lastPingBySession.put(pSession, pPing);
	}

	public synchronized long getLastPing(Session pSession) {
		Long lastPing = lastPingBySession.get(pSession);
		return (lastPing != null) ? lastPing : 0;
	}

	public synchronized Session[] getAllSessions() {
		synchronized (replayClientForSession) {
			return replayClientForSession.keySet().toArray(new Session[0]);
		}
	}

	private static class ReplayClient {
		private final String sharedReplayName;
		private final String coach;

		public ReplayClient(String sharedReplayName, String coach) {
			this.sharedReplayName = sharedReplayName;
			this.coach = coach;
		}
	}

}
