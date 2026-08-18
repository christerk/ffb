package com.fumbbl.ffb.test;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PassTest {

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	@Test
	public void safePassKeepsBallAfterRerolledNaturalOne() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(11, 7)
			.withTeam(true, t -> t
				.player("thrower", p -> p.at(11, 7).stats(6, 3, 3, 5, 8).skill("Safe Pass")))
			.withTeam(false, t -> t
				.player("opponent", p -> p.at(16, 7).stats(6, 3, 3, 5, 8)))
			.build();

		Game game = state.getGame();
		game.getTurnDataHome().setReRolls(1);
		TestRolls.on(state).general(1, 1);

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.PASS_MOVE));
		StepEngine.respond(state, Commands.pass("thrower", new FieldCoordinate(14, 7)));

		IStep step = StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.PASS, ReRollSources.TEAM_RE_ROLL));
		assertNotNull(step);

		Skill safePass = game.getPlayerById("thrower").getSkillWithProperty(
			com.fumbbl.ffb.model.property.NamedProperties.dontDropFumbles);
		step = StepEngine.respond(state, new ClientCommandUseSkill(safePass, true, "thrower", null, false));

		assertNotNull(step);
		assertEquals(new FieldCoordinate(11, 7), game.getFieldModel().getBallCoordinate());
		assertFalse(game.getFieldModel().isBallMoving());
		assertTrue(game.getPlayerById("thrower").isUsed(safePass));
	}
}
