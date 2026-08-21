package com.fumbbl.ffb.test.skill.move;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ShadowingTest {

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	@Test
	public void successfulShadowingDoesNotKeepShadowerAsDefender() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t
				.player("runner", p -> p.at(12, 7).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("shadower", p -> p.at(13, 7).stats(1, 3, 3, 5, 8)
					.skill("Shadowing").skill("Foul Appearance")))
			.build();

		Game game = state.getGame();

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("runner", PlayerAction.MOVE));

		// dodge roll leaving the shadower's tackle zone
		TestRolls.on(state).general(6);
		StepEngine.respond(state, Commands.move("runner", new FieldCoordinate(12, 7), new FieldCoordinate(11, 7)));

		// shadowing roll
		TestRolls.on(state).general(6);
		StepEngine.respond(state,
			Commands.playerChoice(PlayerChoiceMode.SHADOWING, game.getPlayerById("shadower")));

		assertEquals(new FieldCoordinate(11, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertEquals(new FieldCoordinate(12, 7),
			game.getFieldModel().getPlayerCoordinate(game.getPlayerById("shadower")));
		assertNull(game.getDefenderId());

		// the shadower must not be treated as defender anymore, otherwise its foul appearance is rolled for
		// dodge roll for the next square, the shadower cannot shadow again due to its movement of 1
		TestRolls.on(state).general(6);
		StepEngine.respond(state, Commands.move("runner", new FieldCoordinate(11, 7), new FieldCoordinate(10, 7)));

		assertEquals(new FieldCoordinate(10, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertFalse(game.getActingPlayer().hasBlocked());
	}

}
