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

public abstract class TalkHandlerInjury extends TalkHandler {

	public TalkHandlerInjury(CommandAdapter commandAdapter, TalkRequirements.Client requiredClient, TalkRequirements.Environment requiredEnv, TalkRequirements.Privilege... requiresOnePrivilegeOf) {
		super("/injury", 2, commandAdapter, requiredClient, requiredEnv, requiresOnePrivilegeOf);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
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
			if ((player instanceof RosterPlayer)) {
				applyInjury(server, gameState, (RosterPlayer) player, lastingInjury);
			}
		}
	}

	protected abstract void applyInjury(FantasyFootballServer server, GameState gameState, RosterPlayer player, SeriousInjury lastingInjury);
}
