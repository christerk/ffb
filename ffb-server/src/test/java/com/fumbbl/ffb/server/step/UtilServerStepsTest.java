package com.fumbbl.ffb.server.step;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;

import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UtilServerStepsTest {

	@Test
	void acceptsOnlyTheInjuredPlayersCoach() {
		GameState gameState = mock(GameState.class);
		Game game = mock(Game.class);
		Team homeTeam = mock(Team.class);
		FantasyFootballServer server = mock(FantasyFootballServer.class);
		SessionManager sessionManager = mock(SessionManager.class);
		Player<?> homePlayer = mock(Player.class);
		Player<?> awayPlayer = mock(Player.class);
		Session homeSession = mock(Session.class);
		Session awaySession = mock(Session.class);

		when(gameState.getGame()).thenReturn(game);
		when(gameState.getServer()).thenReturn(server);
		when(gameState.getId()).thenReturn(1L);
		when(game.getId()).thenReturn(1L);
		when(game.getTeamHome()).thenReturn(homeTeam);
		when(homeTeam.hasPlayer(homePlayer)).thenReturn(true);
		when(server.getSessionManager()).thenReturn(sessionManager);
		when(sessionManager.getSessionOfHomeCoach(1L)).thenReturn(homeSession);
		when(sessionManager.getSessionOfAwayCoach(1L)).thenReturn(awaySession);

		assertTrue(UtilServerSteps.checkCommandIsFromPlayerTeam(gameState, receivedCommand(homeSession), homePlayer));
		assertFalse(UtilServerSteps.checkCommandIsFromPlayerTeam(gameState, receivedCommand(awaySession), homePlayer));
		assertFalse(UtilServerSteps.checkCommandIsFromPlayerTeam(gameState, receivedCommand(homeSession), awayPlayer));
		assertTrue(UtilServerSteps.checkCommandIsFromPlayerTeam(gameState, receivedCommand(awaySession), awayPlayer));
	}

	private ReceivedCommand receivedCommand(Session session) {
		return new ReceivedCommand(mock(NetCommand.class), session);
	}
}
