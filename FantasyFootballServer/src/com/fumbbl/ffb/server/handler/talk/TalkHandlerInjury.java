package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.factory.SeriousInjuryFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerInjury extends TalkHandler {
	public TalkHandlerInjury() {
		super("/injury", 2, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	public void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();

		SeriousInjuryFactory factory = game.getFactory(FactoryType.Factory.SERIOUS_INJURY);
		for (Player<?> player : findPlayersInCommand(team, commands)) {
			SeriousInjury lastingInjury;
			if ("ni".equalsIgnoreCase(commands[1])) {
				lastingInjury = factory.forAttribute(InjuryAttribute.NI);
			} else if ("-ma".equalsIgnoreCase(commands[1])) {
				lastingInjury = factory.forAttribute(InjuryAttribute.MA);
			} else if ("-av".equalsIgnoreCase(commands[1])) {
				lastingInjury = factory.forAttribute(InjuryAttribute.AV);
			} else if ("-ag".equalsIgnoreCase(commands[1])) {
				lastingInjury = factory.forAttribute(InjuryAttribute.AG);
			} else if ("-st".equalsIgnoreCase(commands[1])) {
				lastingInjury = factory.forAttribute(InjuryAttribute.ST);
			} else if ("-pa".equalsIgnoreCase(commands[1])) {
				lastingInjury = factory.forAttribute(InjuryAttribute.PA);
			} else {
				lastingInjury = null;
			}
			if ((player instanceof RosterPlayer) && (lastingInjury != null)) {
				((RosterPlayer) player).addLastingInjury(lastingInjury);
				server.getCommunication().sendAddPlayer(gameState, team.getId(), (RosterPlayer) player,
					game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
				String info = "Player " + player.getName() + " suffers injury " + lastingInjury.getName() +
					".";
				server.getCommunication().sendPlayerTalk(gameState, null, info);
			}
		}
	}
}
