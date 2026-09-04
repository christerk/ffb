package com.fumbbl.ffb.test.kickoff;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.SpecialRule;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.bb2025.kickoff.StepSwarming;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;

import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Verifies that duplicate or stale end turn commands (e.g. caused by a double clicked end turn button during setup)
 * do not skip the swarming setup.
 */
public class SwarmingEndTurnTest {

	private static final int[][] _HOME_SETUP = {
		{12, 5}, {12, 6}, {12, 7}, {10, 4}, {10, 5}, {10, 6}, {10, 7}, {10, 8}, {10, 9}, {10, 10}, {9, 7}
	};

	private TestServer testServer;
	private Session homeSession;
	private Session awaySession;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
		homeSession = mock(Session.class);
		awaySession = mock(Session.class);
	}

	@Test
	public void endTurnIssuedDuringSetupDoesNotSkipSwarming() {
		GameState state = startSwarming();

		IStep step = StepEngine.respond(state, Commands.endTurn(TurnMode.SETUP), homeSession);

		assertEquals(TurnMode.SWARMING, state.getGame().getTurnMode());
		assertEquals(StepId.SWARMING, step.getId());
	}

	@Test
	public void endTurnOfOtherCoachDoesNotSkipSwarming() {
		GameState state = startSwarming();

		IStep step = StepEngine.respond(state, Commands.endTurn(TurnMode.SWARMING), awaySession);

		assertEquals(TurnMode.SWARMING, state.getGame().getTurnMode());
		assertEquals(StepId.SWARMING, step.getId());
	}

	@Test
	public void endTurnOfSwarmingCoachEndsSwarming() {
		GameState state = startSwarming();

		StepEngine.respond(state, Commands.endTurn(TurnMode.SWARMING), homeSession);

		assertEquals(TurnMode.KICKOFF, state.getGame().getTurnMode());
	}

	@Test
	public void duplicateEndTurnAfterSwarmingIsIgnored() {
		GameState state = startSwarming();

		StepEngine.respond(state, Commands.endTurn(TurnMode.SWARMING), homeSession);
		StepEngine.respond(state, Commands.endTurn(TurnMode.SWARMING), homeSession);

		assertEquals(TurnMode.KICKOFF, state.getGame().getTurnMode());
	}

	private GameState startSwarming() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, team -> {
				for (int index = 0; index < _HOME_SETUP.length; index++) {
					int[] coordinate = _HOME_SETUP[index];
					team.player("home" + index, player -> player.at(coordinate[0], coordinate[1])
						.stats(6, 3, 3, 4, 8).position("blocker"));
				}
				team.player("swarmer1", player -> player.at(-1, 0).stats(6, 2, 3, 4, 7)
					.position("lineman", Keyword.LINEMAN).state(new PlayerState(PlayerState.RESERVE)));
				team.player("swarmer2", player -> player.at(-1, 1).stats(6, 2, 3, 4, 7)
					.position("lineman", Keyword.LINEMAN).state(new PlayerState(PlayerState.RESERVE)));
			})
			.withTeam(false, team -> team.player("away1",
				player -> player.at(20, 7).stats(6, 3, 3, 4, 8).position("blocker")))
			.build();

		Game game = state.getGame();
		game.setHomePlaying(true);
		game.setTurnMode(TurnMode.KICKOFF);
		game.getTeamHome().getSpecialRules().add(SpecialRule.SWARMING);

		registerSessions(state);

		StepSwarming step = new StepSwarming(state);
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(StepParameter.from(StepParameterKey.HANDLE_RECEIVING_TEAM, false));
		step.init(parameters);
		state.getStepStack().push(step);

		TestRolls.on(state).general(2);
		StepEngine.start(state);

		assertEquals(TurnMode.SWARMING, game.getTurnMode());
		return state;
	}

	private void registerSessions(GameState state) {
		SessionManager sessionManager = testServer.getServer().getSessionManager();
		sessionManager.addSession(homeSession, state.getId(), "homeCoach", ClientMode.PLAYER, true,
			Collections.emptyList());
		sessionManager.addSession(awaySession, state.getId(), "awayCoach", ClientMode.PLAYER, false,
			Collections.emptyList());
	}
}
