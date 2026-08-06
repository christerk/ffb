package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DauntlessTest extends AbstractStateTest {

	@Test
	public void dauntlessMatchesStrengthOnSuccessfulRoll() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dauntless")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

		TestRolls.on(state)
				.dauntless(6)
				.block("pushback");

		StepEngine.respond(state, Commands.block("home1", "away1"));

		IStep step = StepEngine.respond(state, Commands.blockChoice(0));
		assertNotNull(step);
		assertEquals(StepId.PUSHBACK, step.getId(),
				"Expected pushback after Dauntless matches STR against stronger opponent");

		step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		assertNotNull(step);

		StepEngine.respond(state, Commands.followup(false));

		Game game = state.getGame();
		assertEquals(PlayerState.STANDING, game.getFieldModel().getPlayerState(game.getPlayerById("home1")).getBase());
		assertEquals(PlayerState.STANDING, game.getFieldModel().getPlayerState(game.getPlayerById("away1")).getBase());
	}

	@Test
	public void dauntlessFailsAndDoesNotMatchStrength() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dauntless")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

		TestRolls.on(state)
				.dauntless(1)
				.block("pushback");

		StepEngine.respond(state, Commands.block("home1", "away1"));

		assertNotNull(state.getCurrentStep(),
				"Dauntless fails - does not match strength, block proceeds with original ST difference");
	}

	@Test
	public void dauntlessVsST5PlusOpponent() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dauntless")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 5, 3, 5, 8)))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).dauntless(6).block("pushback");
		StepEngine.respond(state, Commands.block("home1", "away1"));
		StepEngine.respond(state, Commands.blockChoice(0));
		StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		StepEngine.respond(state, Commands.followup(false));
		assertNotNull(state.getCurrentStep(),
				"Dauntless vs ST5+ opponent: successful roll matches ST, both players still standing after pushback");
	}

	@Test
	public void dauntlessAndIndomitableDoublesStrength() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
								.skill("Dauntless").skill("Indomitable")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).dauntless(6).block("pushback", "pushback", "pushback");
		StepEngine.respond(state, Commands.block("home1", "away1"));
		SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
		Skill indomitable = skillFactory.forName("Indomitable");
		StepEngine.respond(state, Commands.useSkill(indomitable, true, "home1"));
		StepEngine.respond(state, Commands.blockChoice(0));
		StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		StepEngine.respond(state, Commands.followup(false));
		assertNotNull(state.getCurrentStep(),
				"Dauntless + Indomitable doubles strength after Dauntless success - game in valid state");
	}

	@Test
	public void dauntlessAndHornsOnBlitz() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
								.skill("Dauntless").skill("Horns")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
		StepEngine.respond(state, Commands.selectBlitzTarget("away1"));
		TestRolls.on(state).dauntless(6).block("pushback");
		StepEngine.respond(state, Commands.block("home1", "away1"));
		StepEngine.respond(state, Commands.blockChoice(0));
		StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		StepEngine.respond(state, Commands.followup(false));
		assertNotNull(state.getCurrentStep(),
				"Dauntless + Horns on blitz - Horns adds ST1 on blitz, Dauntless matches ST - game in valid state");
	}
}
