package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Arrays;

public class TalkHandlerGames extends TalkHandler {

	public TalkHandlerGames() {
		super("/games", 0, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_SERVER, TalkRequirements.Privilege.DEV);
	}

	@Override
	public void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		String[] response = Arrays.stream(server.getGameCache().findActiveGames().getEntriesSorted())
			.map(entry -> entry.getTeamHomeCoach() + " vs " + entry.getTeamAwayCoach()).toArray(String[]::new);

		server.getCommunication().sendTalk(session, null, response);
	}
}
