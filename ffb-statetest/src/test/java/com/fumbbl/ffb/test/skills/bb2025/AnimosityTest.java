package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AnimosityTest extends AbstractStateTest {

	@Test
	public void passToOtherPlayer() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
			.withBallAt(7, 7)
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 4, 5, 8)
						.skill("Animosity"))
					.player("h2", p -> p.at(10, 7)
						.stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
					.stats(6, 3, 3, 5, 8)))
			.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(6).skill(6).skill(6);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
		assertEquals(new FieldCoordinate(10, 7), g.getGame().getFieldModel().getBallCoordinate(),
			"Ball should be caught at target after Animosity check passes and pass succeeds");
	}

	@Test
	public void animosityCheckFailsCancelsPass() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
			.withBallAt(7, 7)
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 4, 5, 8)
						.skill("Animosity"))
					.player("h2", p -> p.at(10, 7)
						.stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
					.stats(6, 3, 3, 5, 8)))
			.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(1);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
		assertEquals(new FieldCoordinate(7, 7), g.getGame().getFieldModel().getBallCoordinate(),
			"Ball should remain at passer when Animosity check fails");
	}

	@Test
	public void animosityCheckOnHandOffSucceedsOnTwoPlus() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
			.withBallAt(7, 7)
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 4, 5, 8)
						.skill("Animosity"))
					.player("h2", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
					.stats(6, 3, 3, 5, 8)))
			.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(2).skill(6).skill(6);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(8, 7)));
		assertEquals(new FieldCoordinate(8, 7), g.getGame().getFieldModel().getBallCoordinate(),
			"Ball should reach adjacent target after Animosity check passes on 2+");
	}

	@Test
	public void animosityCheckOnHandOffFailsOnOne() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
			.withBallAt(7, 7)
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 4, 5, 8)
						.skill("Animosity"))
					.player("h2", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
					.stats(6, 3, 3, 5, 8)))
			.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(1);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(8, 7)));
		assertEquals(new FieldCoordinate(7, 7), g.getGame().getFieldModel().getBallCoordinate(),
			"Ball should remain at h1 when Animosity check fails on pass attempt");
	}

	@Test
	public void animosityDoesNotTriggerOnThrowTeamMate() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
			.withTeam(true, t -> t
				.player("h1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Animosity").skill("Throw Team-Mate"))
				.player("h2", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
			.withTeam(false, t -> t.player("a1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
			.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.THROW_TEAM_MATE_MOVE));
		TestRolls.on(g).skill(6).scatterDirection(1).scatterDirection(2).scatterDirection(3).skill(6);
		StepEngine.respond(g, Commands.throwTeammate("h1", "h2"));
		StepEngine.respond(g, new com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate("h1", new FieldCoordinate(9, 7)));

		assertEquals(new FieldCoordinate(11, 5),
			g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h2")),
			"TTM sequence completes without Animosity — h2 lands at scattered position");
	}

	@Test
	public void animosityFailureSavedByReroll() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
			.withBallAt(7, 7)
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 4, 5, 8)
						.skill("Animosity")
						.skill("Pro"))
					.player("h2", p -> p.at(10, 7)
						.stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
					.stats(6, 3, 3, 5, 8)))
			.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
		TestRolls.on(g).skill(1).skill(6).skill(2).skill(6).skill(6);
		StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
		StepEngine.respond(g, new ClientCommandUseReRoll(ReRolledActions.ANIMOSITY, ReRollSources.PRO));

		assertEquals(new FieldCoordinate(10, 7), g.getGame().getFieldModel().getBallCoordinate(),
			"Pro reroll saves failed Animosity check and pass completes");
	}
}
