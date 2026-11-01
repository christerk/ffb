package com.fumbbl.ffb.test;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrowdPushTest {

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	@Test
	public void crowdPushWithFanInteractionDoesNotApplyMightyBlow() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t
				.player("home1", p -> p.at(7, 1).stats(6, 3, 3, 5, 8).skill("Mighty Blow")))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(7, 0).stats(6, 3, 3, 5, 8)))
			.build();

		Game game = state.getGame();
		state.getPrayerState().addFanInteraction(game.getTeamHome());

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

		TestRolls.on(state)
			.block("pushback")
			.injury(4, 5);
		StepEngine.respond(state, Commands.block("home1", "away1"));

		IStep step = StepEngine.respond(state, Commands.blockChoice(0));
		if (step != null && step.getId() == StepId.FOLLOWUP) {
			StepEngine.respond(state, Commands.followup(false));
		}

		PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));

		assertEquals(PlayerState.KNOCKED_OUT, defenderState.getBase(),
			"Expected crowd pushed player to be KNOCKED_OUT on an injury roll of 9, was " + defenderState.getBase());
	}
}
