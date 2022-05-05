package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.WeatherFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerGame;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerWeather extends TalkHandler {
	public TalkHandlerWeather() {
		super("/weather", 1, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	public void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();
		Weather weather = new WeatherFactory().forShortName(commands[1]);
		if (weather != null) {
			game.getFieldModel().setWeather(weather);
			server.getCommunication().sendPlayerTalk(gameState, null, "Setting weather to " + game.getFieldModel().getWeather().getName() + ".");
			UtilServerGame.syncGameModel(gameState, null, null, null);
		}
	}
}
