package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChainsawTest extends AbstractStateTest {

	@Test
	public void blockAlternative() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Chainsaw")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
				.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).chainsaw(6).armour(6, 6).injury(3, 2);
		StepEngine.respond(g, Commands.chainsaw("h1", "a1"));
		assertFalse(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isStanding());
	}

	@Test
	public void chainsawArmourPenetrationPlus3() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Chainsaw")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
				.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).chainsaw(6).armour(6, 6).injury(2, 2);
		StepEngine.respond(g, Commands.chainsaw("h1", "a1"));
		Game game = g.getGame();
		PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("a1"));
		assertFalse(defenderState.isStanding(),
				"Expected defender down after Chainsaw +3 armour penetration, was " + defenderState.getBase());
	}

	@Test
	public void chainsawFailsToBreakArmour() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Chainsaw")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 10)))
				.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).chainsaw(1).armour(1, 1);
		StepEngine.respond(g, Commands.chainsaw("h1", "a1"));
		Game game = g.getGame();
		PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("a1"));
		assertEquals(PlayerState.STANDING, defenderState.getBase(),
				"Expected defender standing after Chainsaw fails to break armour, was " + defenderState.getBase());
	}

	@Test
	public void chainsawKickbackOnBlockDieDouble() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Chainsaw")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
				.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).chainsaw(6).armour(5, 5).injury(3, 2);
		StepEngine.respond(g, Commands.chainsaw("h1", "a1"));
		assertNotNull(g.getCurrentStep());
	}

	@Test
	public void chainsawFoulWithPlus3ArmorModifier() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 1)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Chainsaw")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)
						.state(new PlayerState(PlayerState.PRONE).changeActive(true))))
				.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
		TestRolls.on(g).armour(6, 6).injury(3, 2);
		StepEngine.respond(g, Commands.foul("h1", "a1"));
		assertFalse(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isStanding(),
				"Chainsaw foul with +3 armor modifier should break armor and knock down defender");
	}

	@Test
	public void chainsawSecretWeaponEjectionAtEndOfDrive() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Chainsaw")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
				.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).chainsaw(6).armour(6, 6).injury(3, 2);
		StepEngine.respond(g, Commands.chainsaw("h1", "a1"));
		assertFalse(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isStanding(),
				"Chainsaw should not be ejected during drive, only at end of drive");
	}
}
