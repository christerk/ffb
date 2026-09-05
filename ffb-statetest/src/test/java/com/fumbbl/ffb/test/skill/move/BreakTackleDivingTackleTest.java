package com.fumbbl.ffb.test.skill.move;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
import com.fumbbl.ffb.factory.SkillFactory;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BreakTackleDivingTackleTest {

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	@Test
	public void availableBreakTackleCountersDivingTackleWithoutRerollPrompt() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t
				.player("dodger", p -> p.at(12, 7).stats(6, 5, 3, 5, 8)
					.skill("Break Tackle").skill("Dodge")))
			.withTeam(false, t -> t
				.player("tackler", p -> p.at(13, 7).stats(6, 3, 3, 5, 8)
					.skill("Diving Tackle")))
			.build();

		Game game = state.getGame();

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("dodger", PlayerAction.MOVE));

		// dodge roll 3 succeeds without Break Tackle (3+), Diving Tackle (+2) would fail it,
		// but the still available Break Tackle (-3 for ST 5) counters Diving Tackle
		TestRolls.on(state).general(3);
		StepEngine.respond(state, Commands.move("dodger", new FieldCoordinate(12, 7), new FieldCoordinate(11, 7)));

		// no reroll may be offered, instead the diving tackle choice is presented right away
		assertTrue(game.getDialogParameter() instanceof DialogPlayerChoiceParameter,
			"Expected diving tackle player choice but got " + game.getDialogParameter());
		assertEquals(PlayerChoiceMode.DIVING_TACKLE,
			((DialogPlayerChoiceParameter) game.getDialogParameter()).getPlayerChoiceMode());

		StepEngine.respond(state,
			Commands.playerChoice(PlayerChoiceMode.DIVING_TACKLE, game.getPlayerById("tackler")));

		// dodge still succeeds by using Break Tackle, the tackler is dropped in the vacated square
		assertEquals(new FieldCoordinate(11, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("dodger")));
		assertEquals(new FieldCoordinate(12, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("tackler")));
		assertEquals(PlayerState.PRONE, game.getFieldModel().getPlayerState(game.getPlayerById("tackler")).getBase());
		SkillFactory skillFactory = game.getFactory(FactoryType.Factory.SKILL);
		assertTrue(game.getActingPlayer().isSkillUsed(skillFactory.forName("Break Tackle")));
	}

	@Test
	public void rerollOfferedWhenBreakTackleCannotCounterDivingTackle() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t
				.player("dodger", p -> p.at(12, 7).stats(6, 2, 4, 5, 8)
					.skill("Break Tackle").skill("Dodge")))
			.withTeam(false, t -> t
				.player("tackler", p -> p.at(13, 7).stats(6, 3, 3, 5, 8)
					.skill("Diving Tackle")))
			.build();

		Game game = state.getGame();

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("dodger", PlayerAction.MOVE));

		// dodge roll 4 succeeds without Break Tackle (4+), Break Tackle (-1 for ST 2)
		// cannot fully counter Diving Tackle (+2), so a reroll has to be offered
		TestRolls.on(state).general(4);
		StepEngine.respond(state, Commands.move("dodger", new FieldCoordinate(12, 7), new FieldCoordinate(11, 7)));

		assertTrue(game.getDialogParameter() instanceof DialogReRollPropertiesParameter,
			"Expected reroll dialog but got " + game.getDialogParameter());
	}

	@Test
	public void breakTackleUsedOnEarlierDodgeIsNotCounted() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t
				.player("dodger", p -> p.at(12, 7).stats(6, 5, 4, 5, 8)
					.skill("Break Tackle").skill("Dodge")))
			.withTeam(false, t -> t
				.player("marker", p -> p.at(13, 7).stats(6, 3, 3, 5, 8))
				.player("tackler", p -> p.at(10, 8).stats(6, 3, 3, 5, 8)
					.skill("Diving Tackle")))
			.build();

		Game game = state.getGame();

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("dodger", PlayerAction.MOVE));

		// dodge roll 3 into the tackler's zone only succeeds by using Break Tackle (no eligible diving tackler yet)
		TestRolls.on(state).general(3);
		StepEngine.respond(state, Commands.move("dodger", new FieldCoordinate(12, 7), new FieldCoordinate(11, 7)));

		assertEquals(new FieldCoordinate(11, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("dodger")));
		SkillFactory skillFactory = game.getFactory(FactoryType.Factory.SKILL);
		assertTrue(game.getActingPlayer().isSkillUsed(skillFactory.forName("Break Tackle")));

		// dodge roll 4 out of the tackler's zone succeeds on its own, Diving Tackle (+2) would fail it
		// and Break Tackle is already used, so a reroll has to be offered
		TestRolls.on(state).general(4);
		StepEngine.respond(state, Commands.move("dodger", new FieldCoordinate(11, 7), new FieldCoordinate(11, 6)));

		assertTrue(game.getDialogParameter() instanceof DialogReRollPropertiesParameter,
			"Expected reroll dialog but got " + game.getDialogParameter());
	}
}
