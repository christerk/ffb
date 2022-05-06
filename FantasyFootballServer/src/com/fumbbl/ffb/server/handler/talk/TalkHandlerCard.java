package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.CardFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerGame;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerCard extends TalkHandler {

	private static final String _ADD = "add";
	private static final String _REMOVE = "remove";

	public TalkHandlerCard() {
		super("/card", 2, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();
		if (commands.length <= 2) {
			return;
		}
		Card card = gameState.getGame().<CardFactory>getFactory(FactoryType.Factory.CARD).forShortName(commands[2].replace('_', ' '));
		if (card == null) {
			return;
		}
		boolean homeCoach = (team == game.getTeamHome());
		TurnData turnData = homeCoach ? game.getTurnDataHome() : game.getTurnDataAway();
		if (_ADD.equals(commands[1])) {
			turnData.getInducementSet().addAvailableCard(card);
			String info = "Added card " + card.getName() + " for coach " +
				(homeCoach ? game.getTeamHome().getCoach() : game.getTeamAway().getCoach()) + ".";
			server.getCommunication().sendPlayerTalk(gameState, null, info);
		}
		if (_REMOVE.equals(commands[1])) {
			turnData.getInducementSet().removeAvailableCard(card);
			String info = "Removed card " + card.getName() + " for coach " +
				(homeCoach ? game.getTeamHome().getCoach() : game.getTeamAway().getCoach()) + ".";
			server.getCommunication().sendPlayerTalk(gameState, null, info);
		}
		UtilServerGame.syncGameModel(gameState, null, null, null);
	}
}
