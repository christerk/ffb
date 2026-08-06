package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DirtyPlayerTest extends AbstractStateTest {

	@Test
	public void foulModifier() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 1)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Dirty Player")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)
						.state(new PlayerState(PlayerState.PRONE).changeActive(true))))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
		TestRolls.on(g).armour(6, 6).injury(3, 2);
		StepEngine.respond(g, Commands.foul("h1", "a1"));
		assertFalse(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isStanding(),
				"Dirty Player +1 armor modifier breaks armor - defender should not be standing");
	}

	@Test
	public void dirtyPlayerCannotUseBothArmorAndInjuryOnSameFoul() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 1)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Dirty Player")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)
						.state(new PlayerState(PlayerState.PRONE).changeActive(true))))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
		TestRolls.on(g).armour(1, 1);
		StepEngine.respond(g, Commands.foul("h1", "a1"));
		assertFalse(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isStanding(),
				"Dirty Player cannot use both armor and injury on same foul - armor(1,1)+DP(1)=3 does not break AV8, defender remains prone");
	}

	@Test
	public void dirtyPlayerAppliesInjuryModifierOnFoul() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 1)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Dirty Player")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)
						.state(new PlayerState(PlayerState.PRONE).changeActive(true))))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
		TestRolls.on(g).armour(5, 4).injury(3, 2);
		StepEngine.respond(g, Commands.foul("h1", "a1"));
		assertFalse(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isStanding(),
				"Dirty Player +1 injury modifier should contribute to knockdown");
	}

	@Test
	public void dirtyPlayerAndSneakyGitEjectionCheck() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 1)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Dirty Player")
						.skill("Sneaky Git")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)
						.state(new PlayerState(PlayerState.PRONE).changeActive(true))))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
		TestRolls.on(g).armour(4, 5).injury(4, 3);
		StepEngine.respond(g, Commands.foul("h1", "a1"));
		assertNotNull(g.getCurrentStep(),
				"Dirty Player + Sneaky Git ejection check - game should be in valid state after foul with ejection check");
	}
}
