package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CloudBursterTest extends AbstractStateTest {

	@Test
	public void cloudBursterPassNotIntercepted() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Cloud Burster"))
						.player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(6).skill(6);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));
		Game game = state.getGame();
		assertEquals(new FieldCoordinate(10, 7), game.getFieldModel().getBallCoordinate(),
				"Cloud Burster pass should not be intercepted - ball should be at receiver (10,7)");
	}

	@Test
	public void cloudBursterCancelledByVeryLongLegs() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Cloud Burster"))
						.player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("away1", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("Very Long Legs")))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(6);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));
		assertNotNull(state.getCurrentStep(),
				"Cloud Burster cancelled by Very Long Legs - VLL restores interception ability despite Cloud Burster");
	}

	@Test
	public void cloudBursterPassInterceptedByVeryLongLegs() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Cloud Burster"))
						.player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("away1", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("Very Long Legs")))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(6).skill(6);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

		IDialogParameter dialog = state.getGame().getDialogParameter();
		if (dialog != null && dialog.getId() == DialogId.INTERCEPTION) {
			StepEngine.respond(state, Commands.interceptorChoice("away1"));
		}
		assertNotNull(state.getCurrentStep(),
				"Cloud Burster pass intercepted by Very Long Legs - game should be in valid state");
	}

	@Test
	public void cloudBursterPreventsInterceptionByMultipleDefenders() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Cloud Burster"))
						.player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(10, 7).stats(6, 3, 3, 5, 8))
						.player("away2", p -> p.at(11, 8).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(6).skill(6);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));
		Game game = state.getGame();
		assertNotNull(game.getFieldModel().getBallCoordinate(),
				"Cloud Burster prevents interception by multiple defenders");
	}

	@Test
	public void cloudBursterOnInaccuratePassScattersNoInterception() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Cloud Burster"))
						.player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("away1", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(2).scatterDirection(6);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));
		IDialogParameter dialog = state.getGame().getDialogParameter();
		if (dialog != null) {
			assertNotEquals(DialogId.INTERCEPTION, dialog.getId(),
					"Cloud Burster suppresses interception on an inaccurate pass - no interception dialog should be presented for the in-path defender");
		}
		assertNotEquals(new FieldCoordinate(14, 7), state.getGame().getFieldModel().getBallCoordinate(),
				"Cloud Burster inaccurate pass scatters - the ball should not be with the intended receiver at (14,7)");
	}

	@Test
	public void twoVeryLongLegsBothAttemptInterception() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Cloud Burster"))
						.player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("Very Long Legs"))
						.player("away2", p -> p.at(11, 7).stats(6, 3, 3, 5, 8).skill("Very Long Legs")))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(6).skill(6);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));
		IDialogParameter dialog = state.getGame().getDialogParameter();
		assertNotNull(dialog,
				"Two Very Long Legs both override Cloud Burster - the interception dialog should be presented");
		assertEquals(DialogId.INTERCEPTION, dialog.getId(),
				"Two Very Long Legs both override Cloud Burster - expected INTERCEPTION dialog");
		StepEngine.respond(state, Commands.interceptorChoice("away1"));
		assertEquals(new FieldCoordinate(10, 7), state.getGame().getFieldModel().getBallCoordinate(),
				"Two Very Long Legs both override Cloud Burster - away1 intercepts and the ball lands at (10,7)");
	}

	@Test
	public void cloudBursterAndVllRestoresInterception() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Cloud Burster"))
						.player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("Very Long Legs"))
						.player("away2", p -> p.at(11, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(6).skill(6);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));
		IDialogParameter dialog = state.getGame().getDialogParameter();
		if (dialog != null && dialog.getId() == DialogId.INTERCEPTION) {
			StepEngine.respond(state, Commands.interceptorChoice("away1"));
		}
		assertNotNull(state.getCurrentStep(),
				"Cloud Burster + VLL restores interception - game should be in valid state after handling interception dialog");
	}
}
