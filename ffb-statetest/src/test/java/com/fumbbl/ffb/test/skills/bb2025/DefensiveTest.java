package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DefensiveTest extends AbstractStateTest {

	@Test
	public void denyAssists() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
								.stats(6, 3, 3, 5, 8))
						.player("h2", p -> p.at(9, 6)
								.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
								.stats(6, 3, 3, 5, 8)
								.skill("Defensive"))
						.player("a2", p -> p.at(9, 8)
								.stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).block("pushback");
		StepEngine.respond(g, Commands.block("h1", "a1"));
		StepEngine.respond(g, Commands.blockChoice(0));
		StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
		StepEngine.respond(g, Commands.followup(false));
		assertEquals(new FieldCoordinate(9, 7),
				g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
				"Defensive denies assists - defender pushed back to (9,7)");
	}

	@Test
	public void defensiveCancelsGuard() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t
						.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Guard"))
						.player("h2", p -> p.at(9, 6).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t
						.player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Defensive"))
						.player("a2", p -> p.at(9, 8).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).block("pushback");
		StepEngine.respond(g, Commands.block("h1", "a1"));
		StepEngine.respond(g, Commands.blockChoice(0));
		StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
		StepEngine.respond(g, Commands.followup(false));
		assertEquals(new FieldCoordinate(9, 7),
				g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
				"Defensive cancels Guard - despite Guard assist, block still succeeds with pushback to (9,7)");
	}

	@Test
	public void defensivePreventsFoulingAssists() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 1)
				.withTeam(true, t -> t
						.player("h1", p -> p.at(8, 6).stats(6, 3, 3, 5, 8))
						.player("h2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t
						.player("a1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8).skill("Defensive"))
						.player("a2", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
								.state(new PlayerState(PlayerState.PRONE).changeActive(true))))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
		TestRolls.on(g).armour(6, 6).injury(3, 2);
		StepEngine.respond(g, Commands.foul("h1", "a2"));
		assertNotNull(g.getCurrentStep(),
				"Defensive prevents fouling assists - game should be in valid state after foul");
	}

	@Test
	public void defensiveCancelsPutTheBootInFoulAssist() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withBallAt(7, 1)
				.withTeam(true, t -> t
						.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
						.player("h2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("Put the Boot In")))
				.withTeam(false, t -> t
						.player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Defensive")
								.state(new PlayerState(PlayerState.PRONE).changeActive(true))))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
		TestRolls.on(g).armour(6, 6).injury(3, 2);
		StepEngine.respond(g, Commands.foul("h1", "a1"));
		assertNotNull(g.getCurrentStep(),
				"Defensive cancels Put the Boot In foul assist - game should be in valid state");
	}

	@Test
	public void defensiveNegatesGuardAssistInSameTZ() {
		GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t
						.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
						.player("h2", p -> p.at(9, 6).stats(6, 3, 3, 5, 8).skill("Guard")))
				.withTeam(false, t -> t
						.player("a1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8).skill("Defensive"))
						.player("a2", p -> p.at(9, 8).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = g;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).block("pushback");
		StepEngine.respond(g, Commands.block("h1", "a1"));
		StepEngine.respond(g, Commands.blockChoice(0));
		StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
		StepEngine.respond(g, Commands.followup(false));
		assertEquals(new FieldCoordinate(9, 7),
				g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
				"Defensive negates Guard assist - defender pushed back to (9,7) despite Guard");
	}
}
