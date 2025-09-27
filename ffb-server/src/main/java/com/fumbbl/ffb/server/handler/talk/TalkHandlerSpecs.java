package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.ChatCommand;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.SessionManager;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashSet;

@SuppressWarnings("unused")
public class TalkHandlerSpecs extends TalkHandler {
	public TalkHandlerSpecs() {
		super(new HashSet<String>() {{
			add(ChatCommand.SPECS.getCommand());
		}}, 0, TalkRequirements.Client.NONE, TalkRequirements.Environment.NONE);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		long gameId = gameState.getId();
		SessionManager sessionManager = server.getSessionManager();
		boolean issuedBySpec = (sessionManager.getSessionOfHomeCoach(gameId) != session) && (sessionManager.getSessionOfAwayCoach(gameId) != session);

		handleSpecs(server, gameState, session, issuedBySpec);
	}

}
