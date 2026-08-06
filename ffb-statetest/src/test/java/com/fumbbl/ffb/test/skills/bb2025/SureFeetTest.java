package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SureFeetTest extends AbstractStateTest {

	private void move(GameState state, FieldCoordinate... path) {
		FieldCoordinate from = new FieldCoordinate(7, 7);
		for (FieldCoordinate to : path) {
			StepEngine.respond(state, Commands.move("home1", from, to));
			from = to;
		}
	}

	@Test
	public void sureFeetGfiRerollKeepsPlayerMoving() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sure Feet")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));

		TestRolls.on(state)
				.goingForIt(1).goingForIt(2);

		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7));

		Game game = state.getGame();
		FieldCoordinate playerPos = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
		assertEquals(new FieldCoordinate(14, 7), playerPos,
				"Player at " + playerPos + " (Sure Feet rerolled the failed GFI and the player reached the destination)");
	}

	@Test
	public void sureFeetCannotRerollSecondGfi() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sure Feet")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));

		TestRolls.on(state)
				.goingForIt(1).goingForIt(2).goingForIt(1).armour(1, 1);

		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7), new FieldCoordinate(15, 7));

		assertNotNull(state.getCurrentStep());
	}

	@Test
	public void sureFeetRerollsFirstGfiSuccessfullyCannotRerollSecondGfiSameTurn() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sure Feet")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).goingForIt(1).goingForIt(2).goingForIt(2);
		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7), new FieldCoordinate(15, 7));

		assertEquals(new FieldCoordinate(15, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"Sure Feet rerolled the first failed GFI and the second GFI succeeded on its own");
	}

	@Test
	public void sureFeetPlusSprintRerollsThirdGfi() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sure Feet").skill("Sprint")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).goingForIt(2).goingForIt(2).goingForIt(1).goingForIt(2);
		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7), new FieldCoordinate(15, 7), new FieldCoordinate(16, 7));

		assertEquals(new FieldCoordinate(16, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"Sure Feet rerolled the failed third GFI and the player reached (16,7)");
	}

	@Test
	public void sureFeetConsumedForTurnEvenIfRerollFails() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sure Feet")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).goingForIt(1).goingForIt(1).armour(1, 1);
		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7));

		assertNotNull(state.getCurrentStep());
	}

	@Test
	public void sureFeetWorksOnBlitzMoveGfi() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sure Feet")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(15, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
		StepEngine.respond(state, Commands.selectBlitzTarget("away1"));
		// The blitz movement over 7 squares (MA6) needs one goingForIt roll which fails (1) and is rerolled by
		// Sure Feet (2). The BlitzBlock sequence then consumes a further goingForIt roll (StepGoForIt increments
		// currentMove for a BLITZ action, so currentMove 7+1=8 exceeds MA 6) before the block dice are rolled.
		TestRolls.on(state).goingForIt(1).goingForIt(2).goingForIt(2).block("pushback");
		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7));
		StepEngine.respond(state, Commands.block("home1", "away1"));
		IStep step = StepEngine.respond(state, Commands.blockChoice(0));
		assertNotNull(step);
		assertEquals(StepId.PUSHBACK, step.getId(),
				"Sure Feet works on BlitzMove GFI - the blitz block resolves after the GFI reroll");
		StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(16, 7))));
		StepEngine.respond(state, Commands.followup(false));

		assertEquals(new FieldCoordinate(14, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"Sure Feet works on BlitzMove GFI - the failed GFI was rerolled (1 -> 2) and the blitzer reached the square adjacent to the target");
	}
}
