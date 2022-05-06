package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerGame;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerProne extends TalkHandler {
	public TalkHandlerProne() {
		super("/prone", 1, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();

		for (Player<?> player : findPlayersInCommand(team, commands)) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
			if (!playerCoordinate.isBoxCoordinate()) {
				String info = "Player " + player.getName() +
					" placed prone.";
				game.getFieldModel().setPlayerState(player, new PlayerState(PlayerState.PRONE).changeActive(true));
				server.getCommunication().sendPlayerTalk(gameState, null, info);
			}
		}
		UtilServerGame.syncGameModel(gameState, null, null, null);
	}
}
