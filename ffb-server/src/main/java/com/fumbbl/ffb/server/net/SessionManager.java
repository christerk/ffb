package com.fumbbl.ffb.server.net;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.marking.AutoMarkingConfig;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kalimar
 */
public class SessionManager {

	private final Map<Long, Set<Session>> fSessionsByGameId;
	private final Map<Session, JoinedClient> fClientBySession;
	private final Map<Session, Long> fLastPingBySession;
	private final Map<Session, AutoMarkingConfig> autoMarkingBySession;

	public synchronized boolean isSessionDev(Session pSession) {
		JoinedClient client = fClientBySession.get(pSession);
		return client != null && client.hasProperty("DEV");
	}

	public SessionManager() {
		fSessionsByGameId = new HashMap<>();
		fClientBySession = new HashMap<>();
		fLastPingBySession = new HashMap<>();
		autoMarkingBySession = new HashMap<>();
	}

	public synchronized long getGameIdForSession(Session pSession) {
		JoinedClient client = fClientBySession.get(pSession);
		if (client != null) {
			return client.getGameId();
		} else {
			return 0;
		}
	}

	public synchronized String getCoachForSession(Session pSession) {
		JoinedClient client = fClientBySession.get(pSession);
		if (client != null) {
			return client.getCoach();
		} else {
			return null;
		}
	}

	public synchronized boolean isSessionAdmin(Session pSession) {
		JoinedClient client = fClientBySession.get(pSession);
		return client != null && client.hasProperty("ADMIN");
	}

	public synchronized boolean hasEditPrivilege(Session pSession) {
		JoinedClient client = fClientBySession.get(pSession);
		return client != null && client.hasProperty("STATE_EDIT");
	}

	private static class JoinedClient {

		private final long fGameId;
		private final String fCoach;
		private final ClientMode fMode;
		private final boolean fHomeCoach;
		private final List<String> fAccountProperties;

		JoinedClient(long pGameId, String pCoach, ClientMode pMode, boolean pHomeCoach, List<String> pAccountProperties) {
			fGameId = pGameId;
			fCoach = pCoach;
			fMode = pMode;
			fHomeCoach = pHomeCoach;
			fAccountProperties = pAccountProperties;
		}

		public long getGameId() {
			return fGameId;
		}

		public String getCoach() {
			return fCoach;
		}

		public ClientMode getMode() {
			return fMode;
		}

		boolean isHomeCoach() {
			return fHomeCoach;
		}

		public boolean hasProperty(String property) {
			return fAccountProperties.contains(property);
		}

	}

	public synchronized ClientMode getModeForSession(Session pSession) {
		JoinedClient client = fClientBySession.get(pSession);
		if (client != null) {
			return client.getMode();
		} else {
			return null;
		}
	}

	public synchronized Session[] getSessionsForGameId(long pGameId) {
		Set<Session> sessions = fSessionsByGameId.get(pGameId);
		if (sessions != null) {
			return sessions.toArray(new Session[0]);
		} else {
			return new Session[0];
		}
	}

	public synchronized Session getSessionOfHomeCoach(long gameId) {
		Session sessionHomeCoach = null;
		Set<Session> sessions = fSessionsByGameId.get(gameId);
		if (sessions != null) {
			for (Session session : sessions) {
				JoinedClient client = fClientBySession.get(session);
				if ((client != null) && (client.getMode() == ClientMode.PLAYER) && client.isHomeCoach()) {
					sessionHomeCoach = session;
					break;
				}
			}
		}
		return sessionHomeCoach;
	}

	public synchronized boolean isHomeCoach(long gameId, String pCoach) {
		JoinedClient clientHomeCoach = fClientBySession.get(getSessionOfHomeCoach(gameId));
		return ((clientHomeCoach != null) && clientHomeCoach.getCoach().equals(pCoach));
	}
	
	public synchronized Session getSessionOfAwayCoach(long gameId) {
		Session sessionAwayCoach = null;
		Set<Session> sessions = fSessionsByGameId.get(gameId);
		if (sessions != null) {
			for (Session session : sessions) {
				JoinedClient client = fClientBySession.get(session);
				if ((client != null) && (client.getMode() == ClientMode.PLAYER) && !client.isHomeCoach()) {
					sessionAwayCoach = session;
					break;
				}
			}
		}
		return sessionAwayCoach;
	}

	public synchronized boolean isAwayCoach(long gameId, String pCoach) {
		JoinedClient clientAwayCoach = fClientBySession.get(getSessionOfAwayCoach(gameId));
		return ((clientAwayCoach != null) && clientAwayCoach.getCoach().equals(pCoach));
	}

	public synchronized Session[] getSessionsWithoutAwayCoach(long gameId) {
		Set<Session> filteredSessions = new HashSet<>();
		Set<Session> sessions = fSessionsByGameId.get(gameId);
		if ((sessions != null) && (!sessions.isEmpty())) {
			Session sessionAwayCoach = getSessionOfAwayCoach(gameId);
			for (Session session : sessions) {
				if (session != sessionAwayCoach) {
					filteredSessions.add(session);
				}
			}
		}
		return filteredSessions.toArray(new Session[0]);
	}

	public synchronized Session[] getSessionsOfSpectators(long gameId) {
		Set<Session> filteredSessions = new HashSet<>();
		Set<Session> sessions = fSessionsByGameId.get(gameId);
		if ((sessions != null) && (!sessions.isEmpty())) {
			Session sessionAwayCoach = getSessionOfAwayCoach(gameId);
			Session sessionHomeCoach = getSessionOfHomeCoach(gameId);
			for (Session session : sessions) {
				if ((session != sessionAwayCoach) && (session != sessionHomeCoach)) {
					filteredSessions.add(session);
				}
			}
		}
		return filteredSessions.toArray(new Session[0]);
	}

	public synchronized void addSession(Session pSession, long gameId, String pCoach, ClientMode pMode, boolean pHomeCoach, List<String> pAccountProperties) {
		JoinedClient client = new JoinedClient(gameId, pCoach, pMode, pHomeCoach, pAccountProperties);
		fClientBySession.put(pSession, client);
		Set<Session> sessions = fSessionsByGameId.computeIfAbsent(gameId, k -> new HashSet<>());
		sessions.add(pSession);
		fLastPingBySession.put(pSession, System.currentTimeMillis());
	}

	public synchronized void removeSession(Session pSession) {
		long gameId = getGameIdForSession(pSession);
		fClientBySession.remove(pSession);
		fLastPingBySession.remove(pSession);
		autoMarkingBySession.remove(pSession);
		Set<Session> sessions = fSessionsByGameId.get(gameId);
		if (sessions != null) {
			sessions.remove(pSession);
			if (sessions.isEmpty()) {
				fSessionsByGameId.remove(gameId);
			}
		}
	}

	public synchronized void removeAutoMarking(Session session) {
		autoMarkingBySession.remove(session);
	}

	public synchronized void addAutoMarking(Session session, AutoMarkingConfig autoMarkingConfig) {
		autoMarkingBySession.put(session, autoMarkingConfig);
	}

	public synchronized AutoMarkingConfig getAutoMarking(Session session) {
		return autoMarkingBySession.get(session);
	}

	public synchronized void setLastPing(Session pSession, long pPing) {
		fLastPingBySession.put(pSession, pPing);
	}

	public synchronized long getLastPing(Session pSession) {
		Long lastPing = fLastPingBySession.get(pSession);
		return (lastPing != null) ? lastPing : 0;
	}

	public synchronized Session[] getAllSessions() {
		synchronized (fClientBySession) {
			return fClientBySession.keySet().toArray(new Session[0]);
		}
	}

}
