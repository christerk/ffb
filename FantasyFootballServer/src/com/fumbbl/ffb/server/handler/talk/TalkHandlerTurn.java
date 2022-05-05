package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerGame;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerTurn extends TalkHandler {
	public TalkHandlerTurn() {
		super("/turn", 1, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	public void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();
		int newTurnNr = -1;
		try {
			newTurnNr = Integer.parseInt(commands[1]);
		} catch (NumberFormatException ignored) {
		}
		if (newTurnNr >= 0) {
			int turnDiff;
			if (team == game.getTeamHome()) {
				turnDiff = newTurnNr - game.getTurnDataHome().getTurnNr();
			} else {
				turnDiff = newTurnNr - game.getTurnDataAway().getTurnNr();
			}
			game.getTurnDataHome().setTurnNr(game.getTurnDataHome().getTurnNr() + turnDiff);
			game.getTurnDataAway().setTurnNr(game.getTurnDataAway().getTurnNr() + turnDiff);
			server.getCommunication().sendPlayerTalk(gameState, null, "Jumping to turn " + newTurnNr + ".");
			UtilServerGame.syncGameModel(gameState, null, null, null);
		}
	}
}
