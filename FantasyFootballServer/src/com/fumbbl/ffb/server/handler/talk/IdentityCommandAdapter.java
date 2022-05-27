package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.net.SessionManager;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Set;

public class IdentityCommandAdapter implements CommandAdapter {
	@Override
	public Set<String> decorateCommands(Set<String> input) {
		return input;
	}

	@Override
	public Team determineTeam(Game game, SessionManager sessionManager, Session session, String[] commands) {
		return (sessionManager.getSessionOfHomeCoach(game.getId()) == session) ? game.getTeamHome()
			: game.getTeamAway();
	}
}
