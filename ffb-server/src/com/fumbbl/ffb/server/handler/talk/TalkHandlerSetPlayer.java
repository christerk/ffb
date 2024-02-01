package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public abstract class TalkHandlerSetPlayer extends TalkHandler {

	public TalkHandlerSetPlayer(CommandAdapter commandAdapter, TalkRequirements.Client requiredClient, TalkRequirements.Environment requiredEnv, TalkRequirements.Privilege... requiresOnePrivilegeOf) {
		super("/set_player", 3, commandAdapter, requiredClient, requiredEnv, requiresOnePrivilegeOf);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {

		try {
			Player<?> player = team.getPlayerByNr(Integer.parseInt(commands[1]));

			FieldCoordinate coordinate = new FieldCoordinate(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]));

			if (session == server.getSessionManager().getSessionOfAwayCoach(gameState.getId())) {
				coordinate = coordinate.transform();
			}

			movePlayerToCoordinate(server, gameState, player, coordinate);

		} catch (Exception e) {
			// ignored
		}
	}
}
