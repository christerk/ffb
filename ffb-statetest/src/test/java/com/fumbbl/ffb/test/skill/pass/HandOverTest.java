package com.fumbbl.ffb.test.skill.pass;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class HandOverTest {

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	@Test
	public void failedCatchCausesTurnover() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(7, 7)
			.withTeam(true, team -> team
				.player("thrower", player -> player.at(7, 7).stats(6, 3, 3, 5, 8))
				.player("catcher", player -> player.at(8, 7).stats(6, 3, 3, 5, 8)))
			.withTeam(false, team -> team
				.player("opponent", player -> player.at(16, 7).stats(6, 3, 3, 5, 8)))
			.build();

		Game game = state.getGame();
		TestRolls.on(state).general(1);

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.HAND_OVER));
		StepEngine.respond(state, Commands.handOver("thrower", "catcher"));

		assertFalse(game.isHomePlaying());
	}
}
