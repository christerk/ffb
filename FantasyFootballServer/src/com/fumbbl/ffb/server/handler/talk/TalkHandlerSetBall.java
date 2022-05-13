package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public abstract class TalkHandlerSetBall extends TalkHandler {

	public TalkHandlerSetBall(CommandAdapter commandAdapter, TalkRequirements.Client requiredClient, TalkRequirements.Environment requiredEnv, TalkRequirements.Privilege... requiresOnePrivilegeOf) {
		super("/set_ball", 2, commandAdapter, requiredClient, requiredEnv, requiresOnePrivilegeOf);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {

		try {

			FieldCoordinate coordinate = new FieldCoordinate(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));

			if (session == server.getSessionManager().getSessionOfAwayCoach(gameState.getId())) {
				coordinate = coordinate.transform();
			}

			moveBallToCoordinate(server, gameState, coordinate);

		} catch (Exception e) {
			// ignored
		}
	}
}
