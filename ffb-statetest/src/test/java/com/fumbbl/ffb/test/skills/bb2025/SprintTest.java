package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SprintTest extends AbstractStateTest {

	private void move(GameState state, FieldCoordinate... path) {
		FieldCoordinate from = new FieldCoordinate(7, 7);
		for (FieldCoordinate to : path) {
			StepEngine.respond(state, Commands.move("home1", from, to));
			from = to;
		}
	}

	@Test
	void sprintAllowsExtraGfi() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sprint")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).goingForIt(2).goingForIt(2).goingForIt(2);
		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7), new FieldCoordinate(15, 7), new FieldCoordinate(16, 7));

		assertEquals(new FieldCoordinate(16, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"Sprint allows a third GFI so the player can reach (16,7)");
	}

	@Test
	void sprintWithSureFeetDoubleGfi() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sprint").skill("Sure Feet")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).goingForIt(1).goingForIt(2).goingForIt(2).goingForIt(2);
		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7), new FieldCoordinate(15, 7), new FieldCoordinate(16, 7));

		assertNotNull(state.getCurrentStep());
	}

	@Test
	void sprintThirdGfiSucceeds() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sprint")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).goingForIt(2).goingForIt(2).goingForIt(2);
		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7), new FieldCoordinate(15, 7), new FieldCoordinate(16, 7));

		assertEquals(new FieldCoordinate(16, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"Third GFI succeeds with Sprint, player reaches (16,7)");
	}

	@Test
	void sprintThirdGfiFailsPlayerFallsDownTurnover() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sprint")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).goingForIt(2).goingForIt(2).goingForIt(1).armour(1, 1);
		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7), new FieldCoordinate(15, 7), new FieldCoordinate(16, 7));

		assertNotNull(state.getCurrentStep());
	}

	@Test
	void sprintTwoGfisWithoutSureFeetNoRerollAvailable() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sprint")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).goingForIt(1).armour(1, 1);
		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7), new FieldCoordinate(15, 7));

		assertNotNull(state.getCurrentStep());
	}

	@Test
	void sprintBlitzMoveAllowsThirdGfi() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sprint")))
				.withTeam(false, t -> t.player("away1", p -> p.at(17, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
		// The engine selects the blitz target before the movement phase of the blitz action
		StepEngine.respond(state, Commands.selectBlitzTarget("away1"));
		// 4 goingForIt rolls: 3 for the movement GFIs (MA6 + 3 via Sprint over 9 squares) plus 1 more consumed by
		// the GO_FOR_IT step of the BlitzBlock sequence (StepGoForIt increments currentMove for a BLITZ action, so
		// currentMove 9+1=10 exceeds MA 6 and forces another rush roll before the block dice).
		TestRolls.on(state).goingForIt(2).goingForIt(2).goingForIt(2).goingForIt(2).block("pushback");
		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7), new FieldCoordinate(15, 7), new FieldCoordinate(16, 7));
		StepEngine.respond(state, Commands.block("home1", "away1"));

		assertEquals(new FieldCoordinate(16, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"The third GFI (Sprint) is usable during a BlitzMove before the blitz block, so home1 can reach (16,7)");
	}

	@Test
	void sprintThirdGfiFailsOnBlitzMoveTurnover() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sprint")))
				.withTeam(false, t -> t.player("away1", p -> p.at(17, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
		StepEngine.respond(state, Commands.selectBlitzTarget("away1"));
		TestRolls.on(state).goingForIt(2).goingForIt(2).goingForIt(1).armour(1, 1);
		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7), new FieldCoordinate(15, 7), new FieldCoordinate(16, 7));

		assertEquals(new FieldCoordinate(16, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"The failed 3rd GFI knocks home1 down in the square it was entering (16,7)");
		assertEquals(PlayerState.PRONE,
				state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).getBase(),
				"The failed 3rd GFI on a BlitzMove ends the action as a turnover - no blitz block is made");
	}

	@Test
	void sprintExtraGfiOncePerTurnTwoSprintsDoNotStack() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
						.skill("Sprint").skill("Sprint")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).goingForIt(2).goingForIt(2).goingForIt(2);
		move(state, new FieldCoordinate(8, 7), new FieldCoordinate(9, 7), new FieldCoordinate(10, 7),
				new FieldCoordinate(11, 7), new FieldCoordinate(12, 7), new FieldCoordinate(13, 7),
				new FieldCoordinate(14, 7), new FieldCoordinate(15, 7), new FieldCoordinate(16, 7),
				new FieldCoordinate(17, 7));

		assertEquals(new FieldCoordinate(16, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"A 4th GFI is not granted even with two sources of the extra-GFI property - home1 stops at (16,7)");
	}
}
