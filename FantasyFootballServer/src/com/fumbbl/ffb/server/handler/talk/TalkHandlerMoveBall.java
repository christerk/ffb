package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public abstract class TalkHandlerMoveBall extends TalkHandler {

	public TalkHandlerMoveBall(CommandAdapter commandAdapter, TalkRequirements.Client requiredClient, TalkRequirements.Environment requiredEnv, TalkRequirements.Privilege... requiresOnePrivilegeOf) {
		super("/move_ball", 3, commandAdapter, requiredClient, requiredEnv, requiresOnePrivilegeOf);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();

		try {

			FieldCoordinate startCoordinate = game.getFieldModel().getBallCoordinate();

			Direction direction = Direction.forName(commands[2]);

			if (session == server.getSessionManager().getSessionOfAwayCoach(game.getId())) {
				direction = direction.transform();
			}

			FieldCoordinate coordinate = startCoordinate.move(direction, Integer.parseInt(commands[3]));

			moveBallToCoordinate(server, gameState, coordinate);
		} catch (Exception e) {
			// ignored
		}
	}

}
