package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoneHeadTest extends AbstractStateTest {

	@Test
	public void boneHeadFailureEndsAction() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bone Head")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

		TestRolls.on(state).skill(1);

		StepEngine.respond(state, Commands.block("home1", "away1"));

		Game game = state.getGame();
		assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
				"Expected attacker standing when BoneHead roll fails (action ends)");
	}

	@Test
	public void boneHeadSuccessAllowsBlock() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bone Head")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

		TestRolls.on(state).skill(2).block("pushback");

		StepEngine.respond(state, Commands.block("home1", "away1"));

		Game game = state.getGame();
		assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding(),
				"Block should proceed after BoneHead success (roll 2+)");
	}

	@Test
	public void boneHeadSuccessOnRollThree() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bone Head")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

		TestRolls.on(state).skill(3).block("pushback");

		StepEngine.respond(state, Commands.block("home1", "away1"));

		assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
				"Defender should be standing after pushback when BoneHead passes on roll 3 (threshold 2+)");
	}

	@Test
	public void boneHeadSuccessOnRollFour() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bone Head")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

		TestRolls.on(state).skill(4).block("pushback");

		StepEngine.respond(state, Commands.block("home1", "away1"));

		assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
				"Defender should be standing after pushback when BoneHead succeeds on roll 4");
	}

	@Test
	public void boneHeadSuccessWithMoveAction() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bone Head")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(2);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

		assertEquals(new FieldCoordinate(10, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"Player should move after BoneHead passes on roll 2 (MOVE action)");
	}

	@Test
	public void boneHeadFailureOnMoveAction() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bone Head")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(1);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

		assertEquals(new FieldCoordinate(7, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"Attacker should stay at origin when BoneHead fails on MOVE (roll 1 cancels action)");
		assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isConfused(),
				"Attacker should be confused after BoneHead fails");
	}
}
