package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.factory.GameOptionFactory;
import com.fumbbl.ffb.factory.GameOptionIdFactory;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.IGameOption;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerGame;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerOption extends TalkHandler {
	public TalkHandlerOption() {
		super("/option", 1, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	public void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {

		GameOptionId optionId = new GameOptionIdFactory().forName(commands[1]);
		if (optionId == null) {
			return;
		}
		IGameOption gameOption = new GameOptionFactory().createGameOption(optionId);
		if (gameOption == null) {
			return;
		}
		gameOption.setValue(commands[2]);
		gameState.getGame().getOptions().addOption(gameOption);
		String info = "Setting game option " + gameOption.getId().getName() + " to value " +
			gameOption.getValueAsString() + ".";
		server.getCommunication().sendPlayerTalk(gameState, null, info);
		if (gameState.getGame().getStarted() != null) {
			UtilServerGame.syncGameModel(gameState, null, null, null);
		}
	}
}
