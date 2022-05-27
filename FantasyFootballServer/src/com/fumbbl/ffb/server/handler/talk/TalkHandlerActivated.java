package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerGame;
import org.eclipse.jetty.websocket.api.Session;

public abstract class TalkHandlerActivated extends TalkHandler {

	public TalkHandlerActivated(CommandAdapter commandAdapter, TalkRequirements.Client requiredClient, TalkRequirements.Environment requiredEnv, TalkRequirements.Privilege... requiresOnePrivilegeOf) {
		super("/set_activated", 2, commandAdapter, requiredClient, requiredEnv, requiresOnePrivilegeOf);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();

		boolean activated = Boolean.parseBoolean(commands[1]);

		for (Player<?> player : findPlayersInCommand(team, commands)) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
			if (!playerCoordinate.isBoxCoordinate()) {
				String info = "Player " + player.getName() +
					" set " + (activated ? "activated" : "not activated") + ".";
				game.getFieldModel().setPlayerState(player, game.getFieldModel().getPlayerState(player).changeActive(!activated));
				server.getCommunication().sendPlayerTalk(gameState, null, info);
			}
		}
		UtilServerGame.syncGameModel(gameState, null, null, null);
	}
}
