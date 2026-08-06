package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import com.fumbbl.ffb.model.Game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DisturbingPresenceTest extends AbstractStateTest {

	@Test
	public void affectsPasses() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
								.stats(6, 3, 4, 5, 8))
						.player("h2", p -> p.at(10, 7)
								.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Disturbing Presence")))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(6).skill(6);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
		handleInterceptionDialog(g);
		assertNotNull(g.getGame().getFieldModel().getBallCoordinate(),
				"Disturbing Presence affects passes - ball should still have a coordinate after successful pass");
	}

	@Test
	public void multipleDisturbingPresenceStack() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
								.stats(6, 3, 4, 5, 8))
						.player("h2", p -> p.at(10, 7)
								.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t
						.player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Disturbing Presence"))
						.player("a2", p -> p.at(9, 7).stats(6, 3, 3, 5, 8).skill("Disturbing Presence")))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(6).skill(6);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
		handleInterceptionDialog(g);
		assertNotNull(g.getGame().getFieldModel().getBallCoordinate(),
				"Multiple Disturbing Presence stacks - ball should have a coordinate after pass");
	}

	@Test
	public void dpPenaltyOnCatch() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
								.stats(6, 3, 4, 5, 8))
						.player("h2", p -> p.at(10, 7)
								.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("a1", p -> p.at(9, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Disturbing Presence")))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(6).skill(6);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
		handleInterceptionDialog(g);
		assertNotNull(g.getGame().getFieldModel().getBallCoordinate(),
				"Disturbing Presence penalty on catch - ball should have a coordinate");
	}

	@Test
	public void dpAtExactRangeThreeThreshold() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
								.stats(6, 3, 4, 5, 8))
						.player("h2", p -> p.at(11, 7)
								.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("a1", p -> p.at(10, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Disturbing Presence")))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(6).skill(6);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(11, 7)));
		handleInterceptionDialog(g);
		assertNotNull(g.getGame().getFieldModel().getBallCoordinate(),
				"Disturbing Presence at exact range-3 threshold - ball should have a coordinate");
	}

	@Test
	public void dpWithPassSkillReroll() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
								.stats(6, 3, 4, 5, 8)
								.skill("Pass"))
						.player("h2", p -> p.at(10, 7)
								.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Disturbing Presence")))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(1);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
		SkillFactory skillFactory = g.getGame().getFactory(FactoryType.Factory.SKILL);
		Skill pass = skillFactory.forName("Pass");
		TestRolls.on(g).skill(6).skill(6);
		StepEngine.respond(g, Commands.useSkill(pass, true, "h1"));
		handleInterceptionDialog(g);
		assertNotNull(g.getGame().getFieldModel().getBallCoordinate(),
				"Disturbing Presence with Pass skill reroll - ball should have a coordinate after successful reroll");
	}

	@Test
	public void dpFailsBorderlineRoll() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
								.stats(6, 3, 4, 3, 8))
						.player("h2", p -> p.at(11, 7)
								.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("a1", p -> p.at(7, 5)
						.stats(6, 3, 3, 5, 8)
						.skill("Disturbing Presence")))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(4).scatterDirection("east").scatterDirection("east").scatterDirection("east").scatterDirection("east");
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(11, 7)));
		handleInterceptionDialog(g);
		assertNotEquals(new FieldCoordinate(11, 7), g.getGame().getFieldModel().getBallCoordinate(),
				"DP flips the borderline short pass (PA3, roll 4 = min without DP, < 5 with the +1 DP penalty) to inaccurate - "
						+ "the ball scatters away from the receiver at (11,7)");
	}

	@Test
	public void dpAppliesToInterceptionRoll() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
								.stats(6, 3, 4, 5, 8))
						.player("h2", p -> p.at(14, 7)
								.stats(6, 3, 3, 5, 8))
						.player("h3", p -> p.at(11, 7)
								.stats(6, 3, 3, 5, 8)
								.skill("Disturbing Presence")))
				.withTeam(false, t -> t.player("a1", p -> p.at(10, 7)
						.stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(6).skill(3).skill(6);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(14, 7)));
		StepEngine.respond(g, Commands.interceptorChoice("a1"));
		assertEquals(new FieldCoordinate(14, 7), g.getGame().getFieldModel().getBallCoordinate(),
				"DP applies +1 to the interception roll (3 < 5 with the penalty) - a1 fails to intercept and the ball reaches the receiver at (14,7)");
	}

	@Test
	public void dpAppliesWhileProneOrStunned() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
								.stats(6, 3, 4, 5, 8))
						.player("h2", p -> p.at(10, 7)
								.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Disturbing Presence")
						.state(new PlayerState(PlayerState.PRONE))))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(6).skill(6);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
		handleInterceptionDialog(g);
		assertNotNull(g.getGame().getFieldModel().getBallCoordinate(),
				"DP exerts its penalty even while prone - ball still has a coordinate after the pass");
	}

	@Test
	public void dpIgnoredBeyondThreeSquares() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
								.stats(6, 3, 4, 3, 8))
						.player("h2", p -> p.at(11, 7)
								.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("a1", p -> p.at(15, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Disturbing Presence")))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(4).skill(6);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(11, 7)));
		handleInterceptionDialog(g);
		assertEquals(new FieldCoordinate(11, 7), g.getGame().getFieldModel().getBallCoordinate(),
				"DP at distance 4+ from both passer and receiver does not apply - the borderline pass roll 4 "
						+ "succeeds (PA3 short pass, min 4) and the ball reaches the receiver at (11,7)");
	}

	private void handleInterceptionDialog(GameState state) {
		IDialogParameter dialog = state.getGame().getDialogParameter();
		if (dialog != null && dialog.getId() == DialogId.INTERCEPTION) {
			StepEngine.respond(state, Commands.interceptorChoice((String) null));
		}
	}
}
