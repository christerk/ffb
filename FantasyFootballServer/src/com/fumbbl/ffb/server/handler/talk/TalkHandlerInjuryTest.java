package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;

public class TalkHandlerInjuryTest extends TalkHandlerInjury {

	public TalkHandlerInjuryTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	protected void applyInjury(FantasyFootballServer server, GameState gameState, RosterPlayer player, SeriousInjury lastingInjury) {
		Game game = gameState.getGame();
		Team team = player.getTeam();
		player.addLastingInjury(lastingInjury);
		player.updatePosition(player.getPosition(), true, game.getRules(), game.getId());
		server.getCommunication().sendAddPlayer(gameState, team.getId(), player,
			game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
		String info = "Player " + player.getName() + " suffers injury " + lastingInjury.getName() +
			".";
		server.getCommunication().sendPlayerTalk(gameState, null, info);

	}
}
