package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

public class TalkRequirements {

	public enum Client {
		NONE {
			@Override
			public boolean isMet(SessionManager sessionManager, long gameId, Session session) {
				return true;
			}
		},
		PLAYER {
			@Override
			public boolean isMet(SessionManager sessionManager, long gameId, Session session) {
				return hasPlayerSession(sessionManager, gameId, session);
			}
		},
		SPEC {
			@Override
			public boolean isMet(SessionManager sessionManager, long gameId, Session session) {
				return !hasPlayerSession(sessionManager, gameId, session);
			}
		};

		public abstract boolean isMet(SessionManager sessionManager, long gameId, Session session);

		boolean hasPlayerSession(SessionManager sessionManager, long gameId, Session session) {
			return sessionManager.getSessionOfHomeCoach(gameId) == session || sessionManager.getSessionOfAwayCoach(gameId) == session;
		}
	}

	public enum Environment {
		NONE {
			@Override
			public boolean isMet(FantasyFootballServer server, GameState pGameState) {
				return true;
			}
		},
		TEST_GAME {
			@Override
			public boolean isMet(FantasyFootballServer server, GameState pGameState) {
				return isTestMode(server, pGameState);
			}
		},
		TEST_SERVER {
			@Override
			public boolean isMet(FantasyFootballServer server, GameState pGameState) {
				return isServerInTestMode(server);
			}
		};

		public abstract boolean isMet(FantasyFootballServer server, GameState pGameState);

		boolean isServerInTestMode(FantasyFootballServer server) {
			String testSetting = server.getProperty(IServerProperty.SERVER_TEST);
			return StringTool.isProvided(testSetting) && Boolean.parseBoolean(testSetting);
		}

		boolean isTestMode(FantasyFootballServer server, GameState pGameState) {
			return (pGameState.getGame().isTesting()
				|| isServerInTestMode(server));
		}
	}

	public enum Privilege {
		STAFF {
			@Override
			public boolean isMet(SessionManager sessionManager, Session session) {
				return sessionManager.isSessionAdmin(session);
			}
		},
		DEV {
			@Override
			public boolean isMet(SessionManager sessionManager, Session session) {
				return sessionManager.isSessionDev(session);
			}
		};

		public abstract boolean isMet(SessionManager sessionManager, Session session);
	}
}
