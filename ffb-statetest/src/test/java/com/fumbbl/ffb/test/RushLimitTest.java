package com.fumbbl.ffb.test;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RushLimitTest {

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	@Test
	public void moveBeyondRushLimitIsRejected() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(10, 7)
			.withTeam(true, t -> t
				.player("runner", p -> p.at(10, 7).stats(1, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("opponent", p -> p.at(20, 2).stats(6, 3, 3, 5, 8)))
			.build();

		Game game = state.getGame();

		StepEngine.start(state);
		// the ball carrier keeps its pass move action open after running out of movement
		StepEngine.respond(state, Commands.selectPlayer("runner", PlayerAction.PASS_MOVE));

		StepEngine.respond(state, Commands.move("runner", new FieldCoordinate(10, 7), new FieldCoordinate(11, 7)));
		TestRolls.on(state).general(6);
		StepEngine.respond(state, Commands.move("runner", new FieldCoordinate(11, 7), new FieldCoordinate(12, 7)));
		TestRolls.on(state).general(6);
		StepEngine.respond(state, Commands.move("runner", new FieldCoordinate(12, 7), new FieldCoordinate(13, 7)));

		assertEquals(new FieldCoordinate(13, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertEquals(3, game.getActingPlayer().getCurrentMove());

		// a third rush is not allowed and must not be executed even if a client requests it
		TestRolls.on(state).general(6);
		StepEngine.respond(state, Commands.move("runner", new FieldCoordinate(13, 7), new FieldCoordinate(14, 7)));

		assertEquals(new FieldCoordinate(13, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertEquals(3, game.getActingPlayer().getCurrentMove());
		assertEquals(PlayerAction.PASS_MOVE, game.getActingPlayer().getPlayerAction());
	}
}
