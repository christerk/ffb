package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.factory.GameOptionFactory;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerPitch extends TalkHandler {
	public TalkHandlerPitch() {
		super("/pitch", 1, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	public void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		String pitchName = commands[1];
		if (!StringTool.isProvided(pitchName)) {
			return;
		}
		GameOptionFactory gameOptionFactory = new GameOptionFactory();
		String propertyKey = "pitch." + commands[1];
		String pitchUrl = server.getProperty(propertyKey);
		if (StringTool.isProvided(pitchUrl)) {
			gameState.getGame().getOptions().addOption(gameOptionFactory.createGameOption(GameOptionId.PITCH_URL).setValue(pitchUrl));
			server.getCommunication().sendPlayerTalk(gameState, null, "Setting pitch to " + pitchName);
			UtilServerGame.syncGameModel(gameState, null, null, null);
		}
	}
}
