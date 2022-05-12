package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public abstract class TalkHandlerSetPlayer extends TalkHandler {

	public TalkHandlerSetPlayer() {
		super("/set_player", 3, new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();

		try {
			Player<?> player = team.getPlayerByNr(Integer.parseInt(commands[1]));

			FieldCoordinate coordinate = new FieldCoordinate(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]));

			Player<?> occupyingPlayer = game.getFieldModel().getPlayer(coordinate);
			if (occupyingPlayer != null) {
				String info = "Coordinate " + coordinate + " already occupied by " + occupyingPlayer.getName() + ".";
				server.getCommunication().sendPlayerTalk(gameState, null, info);

			} else {
				game.getFieldModel().setPlayerCoordinate(player, coordinate);
				String info = "Set player " + player.getName() + " to coordinate " + coordinate + ".";
				server.getCommunication().sendPlayerTalk(gameState, null, info);
			}
		} catch (NumberFormatException e) {
			// ignored
		}
	}
}
