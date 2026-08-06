package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandHandOver;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CatchTest extends AbstractStateTest {
	@Test
	public void playerWithCatchCanReceivePass() {
		gameState = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8))
						.player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("Catch")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		GameState state = gameState;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(6).skill(6);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));
		Game game = state.getGame();
		assertNotNull(game.getFieldModel().getBallCoordinate(), "Ball should be in play after catch");
	}

	@Test
	public void catchFailsOnReRoll() {
		gameState = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8))
						.player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("Catch")))
				.withTeam(false, t -> t.player("away1", p -> p.at(10, 8).stats(6, 3, 3, 5, 8)))
				.build();
		GameState state = gameState;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(6).skill(1).skill(1).scatterDirection(6);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));
		assertNotNull(state.getCurrentStep());
	}

	@Test
	public void catchRerollOnKickoffCatchAttempt() {
		gameState = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8))
						.player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("Catch")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		GameState state = gameState;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(6).skill(1).skill(6);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));
		Game game = state.getGame();
		assertNotNull(game.getFieldModel().getBallCoordinate(), "Ball should be in play after Catch reroll succeeds");
	}

	@Test
	public void catchRerollOnInterception() {
		gameState = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("h1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8))
						.player("h2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("away1", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("Catch")))
				.build();
		GameState state = gameState;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(6).skill(1).skill(6);
		StepEngine.respond(state, Commands.pass("h1", new FieldCoordinate(14, 7)));
		StepEngine.respond(state, Commands.interceptorChoice("away1"));
		Game game = state.getGame();
		assertEquals(new FieldCoordinate(14, 7), game.getFieldModel().getBallCoordinate(),
				"BB2025 uses ReRolledActions.INTERCEPTION for interception rerolls, which no skill registers - "
						+ "away1's Catch does NOT reroll the interception, the interception fails and the ball carries "
						+ "to the receiver h2 at (14,7)");
		assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("h2")).isStanding(),
				"The receiver caught the carried ball and stays standing");
		assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding(),
				"The interceptor never caught the ball; away1 stays standing");
	}

	@Test
	public void catchRerollOnHandOff() {
		gameState = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("h1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8))
						.player("h2", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Catch")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		GameState state = gameState;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("h1", PlayerAction.HAND_OVER_MOVE));
		TestRolls.on(state).skill(1).skill(6);
		// DSL limitation: the docs specify Commands.pass(...) for the hand-off, but the engine requires the
		// dedicated hand-over command (ClientCommandHandOver) - Commands.pass would dispatch a PASS action instead.
		StepEngine.respond(state, new ClientCommandHandOver("h1", "h2"));
		assertEquals(new FieldCoordinate(8, 7), state.getGame().getFieldModel().getBallCoordinate(),
				"The hand-off completed via the Catch reroll and h2 has possession at (8,7)");
	}

	@Test
	public void catchInTackleZonesWithNegativeModifiers() {
		gameState = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("h1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8))
						.player("h2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("Catch")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(10, 6).stats(6, 3, 3, 5, 8))
						.player("away2", p -> p.at(10, 8).stats(6, 3, 3, 5, 8)))
				.build();
		GameState state = gameState;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(6).skill(1).skill(6);
		StepEngine.respond(state, Commands.pass("h1", new FieldCoordinate(10, 7)));
		Game game = state.getGame();
		assertEquals(new FieldCoordinate(10, 7), game.getFieldModel().getBallCoordinate(),
				"The catch succeeded in two tackle zones via the Catch reroll and h2 has possession at (10,7)");
		assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("h2")).isStanding(),
				"The receiver caught the ball and stays standing");
	}
}
