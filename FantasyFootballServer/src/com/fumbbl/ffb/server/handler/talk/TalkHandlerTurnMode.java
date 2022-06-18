package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TalkHandlerTurnMode extends TalkHandler {

	public TalkHandlerTurnMode(CommandAdapter commandAdapter, TalkRequirements.Client requiredClient, TalkRequirements.Environment requiredEnv, TalkRequirements.Privilege... requiresOnePrivilegeOf) {
		super("/turn_mode", 0, commandAdapter, requiredClient, requiredEnv, requiresOnePrivilegeOf);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();

		List<String> response = new ArrayList<>();
		if (commands.length == 1) {
			response.add("Available TurnModes:");
			Arrays.stream(TurnMode.values()).map(TurnMode::getName).sorted().map(mode -> "  - " + mode).forEach(response::add);
			sendResponseWithCurrentModes(server, session, game, response);
			return;
		}

		TurnMode turnMode = TurnMode.forName(commands[1]);
		if (turnMode != null) {
			game.setTurnMode(turnMode);
		}

		if (commands.length > 3) {
			TurnMode lastTurnMode = TurnMode.forName(commands[2]);
			if (lastTurnMode != null) {
				game.setLastTurnMode(lastTurnMode);
			}
		} else {
			game.setLastTurnMode(null);
		}

		sendResponseWithCurrentModes(server, session, game, response);
	}

	private void sendResponseWithCurrentModes(FantasyFootballServer server, Session session, Game game, List<String> response) {
		if (game.getTurnMode() != null) {
			response.add("Set turnMode to: " + game.getTurnMode().getName());
		}
		if (game.getLastTurnMode() != null) {
			response.add("Set lastTurnMode to: " + game.getLastTurnMode().getName());
		}
		server.getCommunication().sendTalk(session, null, response.toArray(new String[0]));
	}

}
