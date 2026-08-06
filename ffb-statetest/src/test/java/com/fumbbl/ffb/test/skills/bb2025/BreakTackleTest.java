package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BreakTackleTest extends AbstractStateTest {

	@Test
	public void breakTackleAllowsDodgeUsingStrength() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 5, 2, 5, 8).skill("Break Tackle")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8))
						.player("away2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(2);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7),
				new FieldCoordinate(7, 6)));

		Game game = state.getGame();
		FieldCoordinate playerPos = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
		assertEquals(new FieldCoordinate(7, 6), playerPos,
				"Expected Break Tackle player to dodge using STR, was at " + playerPos);
	}

	@Test
	public void breakTackleStrength3UsesStrengthForDodge() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 2, 5, 8).skill("Break Tackle")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8))
						.player("away2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(4);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

		Game game = state.getGame();
		FieldCoordinate playerPos = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
		assertEquals(new FieldCoordinate(7, 6), playerPos,
				"BreakTackle ST 3 dodges using STR on 4+");
	}

	@Test
	public void breakTackleSt5DodgesFromMultipleTz() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 5, 2, 5, 8).skill("Break Tackle")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8))
						.player("away2", p -> p.at(7, 6).stats(6, 3, 3, 5, 8))
						.player("away3", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(2);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(6, 7)));

		Game game = state.getGame();
		assertEquals(new FieldCoordinate(6, 7),
				game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1")),
				"BreakTackle ST 5+ dodges from multiple TZ");
	}

	@Test
	public void breakTackleSt4DodgesOn3() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 4, 2, 5, 8).skill("Break Tackle")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8))
						.player("away2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(3);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

		Game game = state.getGame();
		FieldCoordinate playerPos = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
		assertEquals(new FieldCoordinate(7, 6), playerPos,
				"BreakTackle ST 4 dodges on 3+");
	}

	@Test
	public void breakTackleSt2UsesStInsteadOfAg() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 2, 4, 5, 8).skill("Break Tackle")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(5);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

		assertNotNull(state.getCurrentStep(),
				"BreakTackle ST 2 uses ST instead of AG (dodge threshold 5+, roll 5 succeeds)");
	}

	@Test
	public void breakTackleFailsOnNaturalOne() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 5, 2, 5, 8).skill("Break Tackle")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(1).armour(2, 3);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

		Game game = state.getGame();
		assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
				"BreakTackle natural 1 always fails, player should be prone");
	}

	@Test
	public void breakTackleUsedOncePerTurn() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 5, 2, 5, 8).skill("Break Tackle")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8))
						.player("away2", p -> p.at(7, 5).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(3);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

		assertEquals(new FieldCoordinate(7, 6),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"First dodge should succeed with Break Tackle");
	}
}
