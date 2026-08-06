package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BreatheFireTest extends AbstractStateTest {

	@Test
	public void blockAlternative() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Breathe Fire")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).skill(6).armour(6, 6).injury(3, 2);
		StepEngine.respond(g, Commands.breatheFire("h1", "a1"));
		assertFalse(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isStanding(),
				"BreatheFire armor breaks and defender is not standing");
	}

	@Test
	public void breatheFireKnockDownWithArmourBreak() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Breathe Fire")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).skill(6).armour(1, 1);
		StepEngine.respond(g, Commands.breatheFire("h1", "a1"));
		assertFalse(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isStanding(),
				"BreatheFire roll 6 always knocks down (InjuryTypeBreatheFire)");
	}

	@Test
	public void breatheFireInjuryRollAfterArmorBreak() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Breathe Fire")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).skill(6).armour(6, 6).injury(3, 2);
		StepEngine.respond(g, Commands.breatheFire("h1", "a1"));

		PlayerState defenderState = g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1"));
		assertFalse(defenderState.isStanding(),
				"BreatheFire injury roll after armor break should knock defender down");
	}

	@Test
	public void breatheFireNoEffectOnLowRoll() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Breathe Fire")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).skill(2);
		StepEngine.respond(g, Commands.breatheFire("h1", "a1"));

		assertTrue(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("h1")).isStanding(),
				"Attacker should remain standing (NO_EFFECT)");
		assertTrue(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isStanding(),
				"Defender should remain standing (NO_EFFECT)");
	}

	@Test
	public void breatheFireGrantsSppFromCasualty() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Breathe Fire").skill("Violent Innovator")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).skill(6).armour(6, 6).injury(3, 2);
		StepEngine.respond(g, Commands.breatheFire("h1", "a1"));

		assertNotNull(g.getCurrentStep(),
				"BreatheFire grants SPP from casualty via grantsSppFromSpecialActionsCas property");
	}

	@Test
	public void breatheFireProneOnRollFour() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Breathe Fire")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).skill(4);
		StepEngine.respond(g, Commands.breatheFire("h1", "a1"));

		assertFalse(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isStanding(),
				"BreatheFire roll 4 vs ST<=3 places defender prone (no armour roll)");
	}

	@Test
	public void breatheFireSelfDamageOnNaturalOne() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Breathe Fire")))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).skill(1).armour(5, 5).injury(3, 2);
		StepEngine.respond(g, Commands.breatheFire("h1", "a1"));

		assertFalse(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("h1")).isStanding(),
				"BreatheFire natural 1 causes self-damage (FAILURE), attacker armour broken");
	}
}
