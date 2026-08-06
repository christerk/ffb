package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalSavageryTest extends AbstractStateTest {

	@Test
	void animalSavageryBlockSucceedsWithEasierThreshold() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Animal Savagery")))
			.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
			.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).skill(2).block("pushback");
		IStep step = StepEngine.respond(state, Commands.block("home1", "away1"));

		assertEquals(StepId.BLOCK_ROLL, step.getId(),
			"Animal Savagery on Block passes on 2+ (easier threshold), block proceeds to BLOCK_ROLL");
	}

	@Test
	void animalSavageryFailureEndsActionWithoutAdjacentPlayers() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Animal Savagery")))
			.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
			.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).skill(1);
		StepEngine.respond(state, Commands.block("home1", "away1"));

		assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isConfused(),
			"Failed Animal Savagery with no adjacent teammates marks home1 confused (and inactive) before the action is cancelled");
		assertEquals(new FieldCoordinate(7, 7),
			state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
			"home1 remains at start when Animal Savagery fails and no adjacent teammates — action cancelled");
	}

	@Test
	void animalSavageryOnMoveRequiresHarderThreshold() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Animal Savagery")))
			.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
			.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(3);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

		assertEquals(new FieldCoordinate(7, 7),
			state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
			"Animal Savagery on Move requires 4+, roll 3 fails and cancels the action");
	}

	@Test
	void animalSavageryMoveSucceedsAtHarderThreshold() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Animal Savagery")))
			.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
			.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		TestRolls.on(state).skill(4);
		StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

		assertEquals(new FieldCoordinate(8, 7),
			state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
			"Animal Savagery on Move passes on 4+, move proceeds to destination");
	}

	@Test
	void animalSavageryFailureBitesAdjacentTeammate() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t
				.player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Animal Savagery"))
				.player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 7)))
			.withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
			.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).skill(1).armour(1, 1);
		StepEngine.respond(state, Commands.block("home1", "away1"));

		assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
			"away1 is untouched — the original block against it never resolved");
		assertEquals(new FieldCoordinate(8, 7),
			state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("away1")),
			"away1 remains at its starting square");
		assertEquals(new FieldCoordinate(7, 7),
			state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
			"home1 remains at starting position after failed AS and lash-out onto home2");
		assertEquals(PlayerState.PRONE,
			state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home2")).getBase(),
			"home2 was bitten and knocked down by the lash-out (the block injury downs the target; the armour (1,1) roll held so no further injury)");
		assertEquals(new FieldCoordinate(7, 8),
			state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home2")),
			"home2 remains at its starting square after the lash-out bite");
		assertNull(state.getGame().getDefenderId(),
			"defender id is cleared by StepEndBlocking once the cancelled-block bite flow completes");
	}

	@Test
	void animalSavageryFailureWithMultipleAdjacentTeammatesPromptsChoice() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t
				.player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Animal Savagery"))
				.player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 7))
				.player("home3", p -> p.at(6, 7).stats(6, 3, 3, 5, 7)))
			.withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
			.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).skill(1);
		StepEngine.respond(state, Commands.block("home1", "away1"));
		TestRolls.on(state).armour(1, 1);
		Player<?> home3 = state.getGame().getPlayerById("home3");
		StepEngine.respond(state, new ClientCommandPlayerChoice(PlayerChoiceMode.ANIMAL_SAVAGERY,
			new Player<?>[]{home3}));

		assertEquals(PlayerState.PRONE,
			state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home3")).getBase(),
			"The chosen teammate home3 was bitten and knocked down by the lash-out (the block injury downs the target; the armour (1,1) roll held so no further injury)");
		assertEquals(new FieldCoordinate(6, 7),
			state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home3")),
			"home3 remains at its starting square after the lash-out bite");
		assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home2")).isStanding(),
			"The other adjacent teammate home2 is unaffected (still standing)");
		assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home2")).isConfused(),
			"The other adjacent teammate home2 is not confused");
		assertEquals(new FieldCoordinate(7, 7),
			state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
			"home1 remains at starting position after failed AS and lash-out onto chosen teammate");
		assertNull(state.getGame().getDefenderId(),
			"defender id is cleared by StepEndBlocking once the cancelled-block bite flow completes");
	}
}
