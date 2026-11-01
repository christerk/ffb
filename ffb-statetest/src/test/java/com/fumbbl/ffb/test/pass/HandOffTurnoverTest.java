package com.fumbbl.ffb.test.pass;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HandOffTurnoverTest {

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	@Test
	public void failedCatchWithBallOnGroundCausesTurnover() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(10, 7)
			.withTeam(true, t -> t
				.player("thrower", p -> p.at(10, 7).stats(6, 3, 3, 5, 8))
				.player("catcher", p -> p.at(11, 7).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("opponent", p -> p.at(20, 7).stats(6, 3, 3, 5, 8)))
			.build();

		Game game = state.getGame();
		// catch roll fails, ball bounces north to the empty square (11, 6)
		TestRolls.on(state).general(1, 1);

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.HAND_OVER_MOVE));
		IStep step = StepEngine.respond(state, Commands.handOver("thrower", "catcher"));

		assertNotNull(step);
		assertEquals(new FieldCoordinate(11, 6), game.getFieldModel().getBallCoordinate());
		assertTrue(game.getFieldModel().isBallMoving());
		assertFalse(game.isHomePlaying(), "Failed hand-off catch with ball on the ground must cause a turnover");
	}

	@Test
	public void failedCatchWithBallCaughtByOpponentCausesTurnover() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(10, 7)
			.withTeam(true, t -> t
				.player("thrower", p -> p.at(10, 7).stats(6, 3, 3, 5, 8))
				.player("catcher", p -> p.at(11, 7).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("opponent", p -> p.at(11, 6).stats(6, 3, 3, 5, 8)))
			.build();

		Game game = state.getGame();
		// catch roll fails, ball bounces north to the opponent at (11, 6) who catches it
		TestRolls.on(state).general(1, 1, 6);

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.HAND_OVER_MOVE));
		IStep step = StepEngine.respond(state, Commands.handOver("thrower", "catcher"));

		assertNotNull(step);
		assertEquals(new FieldCoordinate(11, 6), game.getFieldModel().getBallCoordinate());
		assertFalse(game.getFieldModel().isBallMoving());
		assertFalse(game.isHomePlaying(), "Failed hand-off catch with ball caught by an opponent must cause a turnover");
	}

	@Test
	public void failedCatchWithBallCaughtByTeamMateDoesNotCauseTurnover() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(10, 7)
			.withTeam(true, t -> t
				.player("thrower", p -> p.at(10, 7).stats(6, 3, 3, 5, 8))
				.player("catcher", p -> p.at(11, 7).stats(6, 3, 3, 5, 8))
				.player("teamMate", p -> p.at(11, 6).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("opponent", p -> p.at(20, 7).stats(6, 3, 3, 5, 8)))
			.build();

		Game game = state.getGame();
		// catch roll fails, ball bounces north to the team-mate at (11, 6) who catches it
		TestRolls.on(state).general(1, 1, 6);

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.HAND_OVER_MOVE));
		IStep step = StepEngine.respond(state, Commands.handOver("thrower", "catcher"));

		assertNotNull(step);
		assertEquals(new FieldCoordinate(11, 6), game.getFieldModel().getBallCoordinate());
		assertFalse(game.getFieldModel().isBallMoving());
		assertTrue(game.isHomePlaying(), "Failed hand-off catch with ball caught by a team-mate must not cause a turnover");
	}

	@Test
	public void successfulCatchByTargetDoesNotCauseTurnover() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(10, 7)
			.withTeam(true, t -> t
				.player("thrower", p -> p.at(10, 7).stats(6, 3, 3, 5, 8))
				.player("catcher", p -> p.at(11, 7).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("opponent", p -> p.at(20, 7).stats(6, 3, 3, 5, 8)))
			.build();

		Game game = state.getGame();
		TestRolls.on(state).general(6);

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.HAND_OVER_MOVE));
		IStep step = StepEngine.respond(state, Commands.handOver("thrower", "catcher"));

		assertNotNull(step);
		assertEquals(new FieldCoordinate(11, 7), game.getFieldModel().getBallCoordinate());
		assertFalse(game.getFieldModel().isBallMoving());
		assertTrue(game.isHomePlaying(), "Successful hand-off catch must not cause a turnover");
	}

}
