package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PrehensileTailTest extends AbstractStateTest {

	// DSL limitation: GameStateBuilder always builds a home-acting state, but the doc has the dodger on the
	// away team, so homePlaying is flipped to let the away player drive the dodge/leap out of the PT player's TZ.
	private void selectAwayMover(GameState state, boolean jumping) {
		state.getGame().setHomePlaying(false);
		StepEngine.respond(state, Commands.selectPlayer("away1", PlayerAction.MOVE, jumping));
	}

	private FieldCoordinate position(GameState state, String playerId) {
		return state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById(playerId));
	}

	@Test
	public void prehensileTailAddsDodgePenalty() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Prehensile Tail")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		selectAwayMover(state, false);
		TestRolls.on(state).skill(6);
		StepEngine.respond(state, Commands.move("away1", new FieldCoordinate(8, 7), new FieldCoordinate(8, 6)));

		assertEquals(new FieldCoordinate(8, 6), position(state, "away1"),
				"Prehensile Tail adds dodge penalty - away1 (AG 4+) dodged out of the PT player's TZ to (8,6) on roll 6 (target 4 + 1 PT + 1 TZ)");
	}

	@Test
	public void prehensileTailAddsLeapPenalty() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Prehensile Tail")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8).skill("Leap")))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		selectAwayMover(state, true);
		TestRolls.on(state).skill(6);
		StepEngine.respond(state, Commands.move("away1", new FieldCoordinate(8, 7), new FieldCoordinate(10, 7)));

		assertEquals(new FieldCoordinate(10, 7), position(state, "away1"),
				"Prehensile Tail adds leap penalty - away1 (AG 4+) leapt out of the PT player's TZ to (10,7) on roll 6 (target 4 + 1 PT + 1 TZ)");
	}

	@Test
	public void prehensileTailAffectsDodge() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Prehensile Tail")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		selectAwayMover(state, false);
		TestRolls.on(state).skill(6);
		StepEngine.respond(state, Commands.move("away1", new FieldCoordinate(8, 7), new FieldCoordinate(8, 6)));

		assertEquals(new FieldCoordinate(8, 6), position(state, "away1"),
				"Prehensile Tail affects dodge - away1 (AG 4+) succeeded on the PT-affected dodge (target 6) to (8,6)");
	}

	@Test
	public void makesDodgingHarderPlusDivingTackleStackingOnDodge() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Prehensile Tail").skill("Diving Tackle")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		selectAwayMover(state, false);
		// The engine only offers Diving Tackle when the dodger leaves the DT user's tacklezone
		// (GameOptionId.DIVING_TACKLE_LEAVING_TZ_ONLY), so away1 dodges to (9,7) which is NOT adjacent to
		// home1 at (7,7). The stacked penalties (target 4 + 1 PT + 2 DT = 7) make roll 6 fail, so the
		// DIVING_TACKLE choice dialog is offered and must be answered with home1 before the dodge is
		// re-evaluated with the -2 modifier and away1 falls.
		TestRolls.on(state).skill(5).armour(1, 1);
		StepEngine.respond(state, Commands.move("away1", new FieldCoordinate(8, 7), new FieldCoordinate(9, 7)));
		Player<?> home1 = state.getGame().getPlayerById("home1");
		StepEngine.respond(state, new ClientCommandPlayerChoice(PlayerChoiceMode.DIVING_TACKLE,
				new Player<?>[]{home1}));

		assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
				"Makes dodging harder plus Diving Tackle stacking on dodge - the stacked PT + DT penalties failed the dodge (roll 5 < min 6) and away1 fell");
	}

	@Test
	public void makesJumpingHarderPlusDivingTackleStackingOnLeap() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Prehensile Tail").skill("Diving Tackle")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8).skill("Leap")))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		selectAwayMover(state, true);
		// The leap to (10,7) leaves home1's tacklezone but the landing square is not adjacent to home1,
		// so Diving Tackle is offered (DIVING_TACKLE_LEAVING_TZ_ONLY). The stacked penalties (target
		// 4 + 1 PT + 1 TZ + 2 DT = 8) fail roll 6 once the DIVING_TACKLE choice is answered.
		TestRolls.on(state).skill(5).armour(1, 1);
		StepEngine.respond(state, Commands.move("away1", new FieldCoordinate(8, 7), new FieldCoordinate(10, 7)));
		Player<?> home1 = state.getGame().getPlayerById("home1");
		StepEngine.respond(state, new ClientCommandPlayerChoice(PlayerChoiceMode.DIVING_TACKLE,
				new Player<?>[]{home1}));

		assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
				"Makes jumping harder plus Diving Tackle stacking on leap - the stacked PT + DT penalties failed the leap (roll 5 < min 6) and away1 fell");
	}

	@Test
	public void prehensileTailVsTwoHeadsCancelsToNetZero() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Prehensile Tail")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8).skill("Two Heads")))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		selectAwayMover(state, false);
		TestRolls.on(state).skill(5);
		StepEngine.respond(state, Commands.move("away1", new FieldCoordinate(8, 7), new FieldCoordinate(8, 6)));

		assertEquals(new FieldCoordinate(8, 6), position(state, "away1"),
				"Prehensile Tail vs Two Heads cancels to net zero - Two Heads +1 offsets the PT +1 (target 4 + 1 TZ + 1 PT - 1 = 5), away1 dodged to (8,6) on roll 5");
	}

	@Test
	public void prehensileTailVsTitchyDodgeNoEffect() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Prehensile Tail")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 2, 3, 5, 6).skill("Titchy")))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		selectAwayMover(state, false);
		// Leaving the PT player's tackle zone is still a dodge (a dodge roll is always required). Titchy's -1
		// DodgeModifier offsets part of the PT penalty, but home1's tackle zone on the target square still
		// applies (DodgeModifierFactory.numberOfTacklezones only skips opponents that themselves have
		// hasNoTacklezoneForDodging). Target = max(2, AG 3 + 1 PT + 1 TZ - 1 Titchy) = 4, so D6=4 succeeds.
		TestRolls.on(state).skill(4);
		StepEngine.respond(state, Commands.move("away1", new FieldCoordinate(8, 7), new FieldCoordinate(8, 6)));

		assertEquals(new FieldCoordinate(8, 6), position(state, "away1"),
				"Prehensile Tail vs Titchy dodge no effect - away1 moved out of the PT player's TZ to (8,6)");
	}
}
