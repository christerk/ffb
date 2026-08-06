package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BloodlustTest extends AbstractStateTest {

	@Test
	void bloodlustNeedsToDrinkBlood() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Bloodlust")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(2);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

		assertEquals(new FieldCoordinate(10, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"home1 should move to destination when Bloodlust check passes");
	}

	@Test
	void bloodlustFailsAndEndsAction() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Bloodlust"))
						.player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(1);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

		assertEquals(new FieldCoordinate(7, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"home1 should remain at starting position when Bloodlust check fails");
	}

	@Test
	void bloodlustOnBlockPassesAtThresholdTwo() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Bloodlust")))
				.withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).skill(2).block("pushback");
		StepEngine.respond(state, Commands.block("home1", "away1"));

		IStep step = StepEngine.respond(state, Commands.blockChoice(0));
		assertNotNull(step);
		assertEquals(StepId.PUSHBACK, step.getId());

		step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		assertNotNull(step);

		StepEngine.respond(state, Commands.followup(false));

		assertEquals(new FieldCoordinate(9, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("away1")),
				"Defender should be at pushback position after Bloodlust passes and block proceeds");
	}

	@Test
	void bloodlustFailureWithNonThrallTeammateDoesNotBite() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Bloodlust"))
						.player("home2", p -> p.at(6, 7).stats(6, 3, 3, 5, 7)))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(1);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

		assertEquals(new FieldCoordinate(7, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"home1 should stay at starting position after Bloodlust fails");
		assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home2")).isStanding(),
				"home2 is not a Thrall, so no bite occurs on a failed Bloodlust");
	}

	@Test
	void bloodlustFailureWithoutAdjacentTeammateEndsAction() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Bloodlust")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(1);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

		assertEquals(new FieldCoordinate(7, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"home1 should remain at starting position when Bloodlust fails with no adjacent teammate");
	}

	@Test
	void bloodlustWithProReroll() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8)
						.skill("Bloodlust").skill("Pro")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(1).skill(6).skill(2);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.BLOOD_LUST, ReRollSources.PRO));

		assertEquals(new FieldCoordinate(10, 7),
				state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
				"home1 should reach destination when Pro reroll saves failed Bloodlust check");
	}
}
