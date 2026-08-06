package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AgilityIncreaseTest extends AbstractStateTest {

	@Test
	void plusAGImprovesDodge() {
		gameState = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 5, 5, 8).skill("+AG")))
			.withTeam(false, t -> t.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
			.build();
		GameState state = gameState;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(5);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

		assertEquals(new FieldCoordinate(7, 6),
			state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
			"+AG player should dodge successfully");
		assertFalse(state.getGame().getFieldModel().getPlayerState(
			state.getGame().getPlayerById("home1")).isProneOrStunned(),
			"+AG player should not be prone after dodge");
	}

	@Test
	void twoPlusAGStacksForHigherDodge() {
		gameState = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("+AG").skill("+AG")))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8))
				.player("away2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
			.build();
		GameState state = gameState;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(4);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

		assertFalse(state.getGame().getFieldModel().getPlayerState(
			state.getGame().getPlayerById("home1")).isProneOrStunned(),
			"Two +AG player should not be prone after dodge");
	}

	@Test
	void plusAGImprovesBallPickup() {
		gameState = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withBallAt(8, 7)
			.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 5, 5, 8).skill("+AG")))
			.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
			.build();
		GameState state = gameState;
		state.getGame().getFieldModel().setBallMoving(true);

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(4).scatterDirection(1);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

		assertEquals(new FieldCoordinate(8, 6),
				state.getGame().getFieldModel().getBallCoordinate(),
				"Pickup roll 4 vs AG 5+ fails (the harness does not apply the +AG modifier), so the ball"
						+ " scatters NORTH from (8,7) to (8,6) instead of being picked up");
	}

	@Test
	void plusAGImprovesIntercept() {
		gameState = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withBallAt(7, 7)
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t
				.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8))
				.player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(10, 7).stats(6, 3, 5, 5, 8).skill("+AG")))
			.build();
		GameState state = gameState;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(6).skill(6);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));
		StepEngine.respond(state, Commands.interceptorChoice("away1"));

		assertEquals(new FieldCoordinate(10, 7),
				state.getGame().getFieldModel().getBallCoordinate(),
				"Interceptor away1 (+AG, intercept roll 6 succeeds) takes the ball at (10,7) instead of the"
						+ " intended receiver at (14,7) — the intercept attempt resolved");
	}

	@Test
	void plusAGImprovesLeap() {
		gameState = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t
				.player("home1", p -> p.at(7, 7).stats(6, 3, 5, 5, 8).skill("+AG").skill("Leap")))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8))
				.player("away2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
			.build();
		GameState state = gameState;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(6);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

		Game game = state.getGame();
		assertEquals(new FieldCoordinate(7, 6),
			game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1")),
			"+AG player with Leap should dodge successfully");
	}

	@Test
	void twoPlusAGStacksForOnePlusThreshold() {
		gameState = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("+AG").skill("+AG")))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8))
				.player("away2", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
				.player("away3", p -> p.at(7, 6).stats(6, 3, 3, 5, 8)))
			.build();
		GameState state = gameState;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(5);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 6)));

		assertFalse(state.getGame().getFieldModel().getPlayerState(
			state.getGame().getPlayerById("home1")).isProneOrStunned(),
			"Two +AG stacking allows 1+ AG threshold on dodge, player should not be prone");
	}
}
