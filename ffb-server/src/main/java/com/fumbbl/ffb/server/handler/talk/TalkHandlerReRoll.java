package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public abstract class TalkHandlerReRoll extends TalkHandler {

	public TalkHandlerReRoll(CommandAdapter commandAdapter, TalkRequirements.Client requiredClient, TalkRequirements.Environment requiredEnv, TalkRequirements.Privilege... requiresOnePrivilegeOf) {
		super("/set_rerolls", 1, commandAdapter, requiredClient, requiredEnv, requiresOnePrivilegeOf);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		try {
			Game game = gameState.getGame();
			int newValue = Math.max(0, Math.min(Integer.parseInt(commands[1]), 9));
			if (team == game.getTeamHome()) {
				game.getTurnDataHome().setReRolls(newValue);
			} else {
				game.getTurnDataAway().setReRolls(newValue);
			}
		} catch (Exception e) {
			// ignored
		}
	}

}
