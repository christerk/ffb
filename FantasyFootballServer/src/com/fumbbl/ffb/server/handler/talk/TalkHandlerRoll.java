package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.DiceCategory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TalkHandlerRoll extends TalkHandler {
	public TalkHandlerRoll() {
		super("/roll", 0, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	public void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();
		if (commands.length > 1) {
			if ("clear".equals(commands[1])) {
				gameState.getDiceRoller().clearTestRolls();
			} else {
				for (int i = 1; i < commands.length; i++) {
					try {
						gameState.getDiceRoller().addTestRoll(commands[i], game, team);
					} catch (NumberFormatException ignored) {
					}
				}
			}
		}

		Map<String, List<DiceCategory>> testRolls = gameState.getDiceRoller().getTestRolls();
		if (testRolls.size() > 0) {
			testRolls.forEach((key, rolls) -> {
				List<String> strings = rolls.stream().map(x -> x.text(game)).collect(Collectors.toList());
				String result = "Next " + key + " rolls will be: " + String.join(", ", strings);
				server.getCommunication().sendPlayerTalk(gameState, null, result);
			});
		} else {
			server.getCommunication().sendPlayerTalk(gameState, null, "Next dice rolls will be random.");
		}
	}
}
