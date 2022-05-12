package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerPlayingLive extends TalkHandler {

	public TalkHandlerPlayingLive() {
		super("/playing", 0, new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {

		Game game = gameState.getGame();
		game.setHomePlaying(team == game.getTeamHome());

		server.getCommunication().sendPlayerTalk(gameState, null, "Set playing team to " + team.getName() + ".");
	}
}
