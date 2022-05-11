package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerGame;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerTurnLive extends TalkHandler {

	public TalkHandlerTurnLive() {
		super("/turn", 1, new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();
		int newTurnNr = -1;
		try {
			newTurnNr = Integer.parseInt(commands[1]);
		} catch (NumberFormatException ignored) {
		}
		if (newTurnNr >= 0) {
			if (team == game.getTeamHome()) {
				game.getTurnDataHome().setTurnNr(newTurnNr);
			} else {
				game.getTurnDataAway().setTurnNr(newTurnNr);
			}
			server.getCommunication().sendPlayerTalk(gameState, null, "Jumping to turn " + newTurnNr + " for " + team.getName() + ".");
			UtilServerGame.syncGameModel(gameState, null, null, null);
		}
	}
}
