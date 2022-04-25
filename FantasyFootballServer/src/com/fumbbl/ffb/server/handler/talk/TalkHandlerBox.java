package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.factory.SeriousInjuryFactory;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.server.util.UtilServerGame;

public class TalkHandlerBox extends TalkHandler {

	public TalkHandlerBox() {
		super("/box", 2);
	}

	@Override
	public void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team) {

		Game game = gameState.getGame();
		ServerCommunication communication = server.getCommunication();

		RollMechanic mechanic = ((RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name()));
		for (Player<?> player : findPlayersInCommand(team, commands, 2)) {
			if ("rsv".equalsIgnoreCase(commands[1])) {
				putPlayerIntoBox(gameState, communication, player, new PlayerState(PlayerState.RESERVE), "Reserve", null);
			} else if ("ko".equalsIgnoreCase(commands[1])) {
				putPlayerIntoBox(gameState, communication, player, new PlayerState(PlayerState.KNOCKED_OUT), "Knocked Out", null);
			} else if ("bh".equalsIgnoreCase(commands[1])) {
				putPlayerIntoBox(gameState, communication, player, new PlayerState(PlayerState.BADLY_HURT), "Badly Hurt", null);
			} else if ("si".equalsIgnoreCase(commands[1])) {
				int[] roll = mechanic.rollCasualty(gameState.getDiceRoller());
				SeriousInjury seriousInjury = mechanic.interpretSeriousInjuryRoll(game, new InjuryContext(), roll);
				putPlayerIntoBox(gameState, communication, player, new PlayerState(PlayerState.SERIOUS_INJURY), "Serious Injury",
					seriousInjury);
			} else if ("rip".equalsIgnoreCase(commands[1])) {
				putPlayerIntoBox(gameState, communication, player, new PlayerState(PlayerState.RIP), "RIP", ((SeriousInjuryFactory) game.getFactory(FactoryType.Factory.SERIOUS_INJURY)).dead());
			} else if ("ban".equalsIgnoreCase(commands[1])) {
				putPlayerIntoBox(gameState, communication, player, new PlayerState(PlayerState.BANNED), "Banned", null);
			} else {
				break;
			}
		}
		UtilServerGame.syncGameModel(gameState, null, null, null);
	}
}
