package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.IGameOption;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TalkHandlerOptions extends TalkHandler {
	public TalkHandlerOptions() {
		super("/options", 0, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	public void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();
		List<IGameOption> optionList = new ArrayList<>();
		for (GameOptionId optionId : GameOptionId.values()) {
			optionList.add(game.getOptions().getOptionWithDefault(optionId));
		}
		optionList.sort(Comparator.comparing(pO -> pO.getId().getName()));
		for (IGameOption option : optionList) {
			server.getCommunication().sendPlayerTalk(gameState, null, "Option " + option.getId().getName() + " = " + option.getValueAsString());
		}
	}
}
