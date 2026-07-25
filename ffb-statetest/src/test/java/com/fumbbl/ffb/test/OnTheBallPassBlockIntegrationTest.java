package com.fumbbl.ffb.test;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OnTheBallPassBlockIntegrationTest {

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	@Test
	public void onTheBallTriggersPassBlockWhenOpponentDeclaresPass() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(11, 7)
			.withTeam(true, t -> t
				.player("home1", p -> p.at(11, 7).stats(6, 3, 3, 2, 8)))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(16, 7).stats(6, 3, 3, 5, 8).skill("On The Ball")))
			.build();

		Game game = state.getGame();
		IStep step = StepEngine.start(state);
		assertNotNull(step, "Expected a step after start");

		step = StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		assertNotNull(step, "Expected a step after selectPlayer");

		step = StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));
		assertNotNull(step, "Expected a step after pass command");

		assertEquals(TurnMode.PASS_BLOCK, game.getTurnMode(),
			"Expected PASS_BLOCK turn mode after pass with On The Ball defender");
		assertFalse(game.isHomePlaying(),
			"Expected away team to be playing during PASS_BLOCK");
		assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isActive(),
			"Expected away player with On The Ball to be active");
	}

	@Test
	public void onTheBallDoesNotTriggerWhenDefenderLacksSkill() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(11, 7)
			.withTeam(true, t -> t
				.player("home1", p -> p.at(11, 7).stats(6, 3, 3, 2, 8)))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(16, 7).stats(6, 3, 3, 5, 8)))
			.build();

		Game game = state.getGame();
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		IStep step = StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

		assertNotNull(step, "Expected a step after pass command");
		assertNotEquals(TurnMode.PASS_BLOCK, game.getTurnMode(),
			"Expected turn mode to NOT be PASS_BLOCK when defender lacks the skill");
	}

	@Test
	public void onTheBallDoesNotTriggerWhenDefenderHasNoTacklezones() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(11, 7)
			.withTeam(true, t -> t
				.player("home1", p -> p.at(11, 7).stats(6, 3, 3, 2, 8)))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(16, 7).stats(6, 3, 3, 5, 8)
					.skill("On The Ball")
					.state(new PlayerState(PlayerState.PRONE).changeActive(true))))
			.build();

		Game game = state.getGame();
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		IStep step = StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

		assertNotNull(step, "Expected a step after pass command");
		assertNotEquals(TurnMode.PASS_BLOCK, game.getTurnMode(),
			"Expected turn mode to NOT be PASS_BLOCK when defender has no tacklezones");
	}

	@Disabled("Requires removing DUMP_OFF guard in StepPassBlock.executeStep()")
	@Test
	public void blockOnThrowerWithDumpOffTriggersPassBlock() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(11, 7)
			.withTeam(true, t -> t
				.player("home_blitzer", p -> p.at(10, 7).stats(6, 3, 3, 5, 8))
				.player("home_otb", p -> p.at(16, 7).stats(6, 3, 3, 5, 8).skill("On The Ball")))
			.withTeam(false, t -> t
				.player("away_thrower", p -> p.at(11, 7).stats(6, 3, 3, 2, 8).skill("Dump-Off")))
			.build();

		Game game = state.getGame();
		StepEngine.start(state);

		StepEngine.respond(state, Commands.selectPlayer("home_blitzer", PlayerAction.BLOCK));
		StepEngine.respond(state, Commands.block("home_blitzer", "away_thrower"));

		Skill dumpOff = game.getPlayerById("away_thrower").getSkills()[0];
		StepEngine.respond(state, new ClientCommandUseSkill(dumpOff, true, "away_thrower", null, false));

		IStep step = StepEngine.respond(state, Commands.pass("away_thrower", new FieldCoordinate(14, 7)));
		assertNotNull(step, "Expected a step after pass command");

		assertEquals(TurnMode.PASS_BLOCK, game.getTurnMode(),
			"PASS_BLOCK should be triggered during dump-off pass sequence");
	}
}
