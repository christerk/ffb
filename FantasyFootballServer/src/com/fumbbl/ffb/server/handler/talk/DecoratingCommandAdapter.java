package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class DecoratingCommandAdapter implements CommandAdapter {

	private static final String HOME = "home";
	private static final String AWAY = "away";
	private static final String TEAM_DELIM = "_";

	@Override
	public Set<String> decorateCommands(Set<String> input) {
		return input.stream()
			.flatMap(command -> Arrays.stream(new String[]{command + TEAM_DELIM + HOME, command + TEAM_DELIM + AWAY}))
			.collect(Collectors.toSet());
	}

	@Override
	public Team determineTeam(Game game, SessionManager sessionManager, Session session, String[] commands) {
		String command = commands[0];
		if (!StringTool.isProvided(command)) {
			throw new FantasyFootballException("No command given");
		}

		String[] commandParts = command.split(TEAM_DELIM);

		if (commandParts.length != 2) {
			throw new FantasyFootballException("Unsupported format for command: " + command);
		}

		switch (commandParts[1].toLowerCase()) {
			case HOME:
				return game.getTeamHome();
			case AWAY:
				return game.getTeamAway();
			default:
				throw new FantasyFootballException("Invalid team: " + commandParts[1]);
		}
	}
}
