package com.fumbbl.ffb.server.net;

import com.fumbbl.ffb.server.marking.AutoMarkingConfig;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
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

	public synchronized void addSession(Session session, String name, String pCoach) {
		ReplayClient client = new ReplayClient(name, pCoach);
		replayClientForSession.put(session, client);
		Set<Session> sessions = sessionsForReplay.computeIfAbsent(name, k -> new HashSet<>());
		client.setControl(sessions.isEmpty());
		sessions.add(session);
		lastPingBySession.put(session, System.currentTimeMillis());
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
		return sessionsForReplay.containsKey(replayName) ? sessionsForReplay.get(replayName).toArray(new Session[0]) : null;
	}

	public synchronized String replayForSession(Session session) {
		ReplayClient replayClient = replayClientForSession.get(session);
		if (replayClient == null) {
			return "";
		}
		return replayClient.sharedReplayName;
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
		return replayClientForSession.containsKey(session) || autoMarkingBySession.containsKey(session);
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

	public synchronized boolean hasControl(Session session) {
		ReplayClient client = replayClientForSession.get(session);
		return client != null && client.hasControl();
	}

	public synchronized String controllingCoach(Session session) {
		String replayName = replayNameForSession(session);
		return Arrays.stream(sessionsForReplay(replayName))
			.map(replayClientForSession::get)
			.filter(ReplayClient::hasControl)
			.map(ReplayClient::getCoach)
			.findFirst().orElse("");
	}

	public synchronized boolean transferControl(Session controllingSession, String coach) {
		Set<Session> sessions = otherSessions(controllingSession);
		Optional<Session> futureControllingSession = sessions.stream().filter(session -> coach.equals(coach(session))).findFirst();
		if (hasControl(controllingSession) && futureControllingSession.isPresent()) {
			replayClientForSession.get(controllingSession).setControl(false);
			replayClientForSession.get(futureControllingSession.get()).setControl(true);
			return true;
		}
		return false;
	}

	private static class ReplayClient {
		private final String sharedReplayName;
		private final String coach;
		private boolean control;

		public ReplayClient(String sharedReplayName, String coach) {
			this.sharedReplayName = sharedReplayName;
			this.coach = coach;
		}

		public String getCoach() {
			return coach;
		}

		public boolean hasControl() {
			return control;
		}

		public void setControl(boolean control) {
			this.control = control;
		}
	}

}
