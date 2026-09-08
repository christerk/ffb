package com.fumbbl.ffb.test.skill.move;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the BB2025 dialog that lets the coach spend optional dodge modifiers to rescue a failed dodge.
 */
public class DodgeModifierChoiceTest {

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	private GameState buildState(String... dodgerSkills) {
		return new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t
				.player("runner", p -> {
					p.at(12, 7).stats(6, 3, 3, 5, 8);
					for (String skill : dodgerSkills) {
						p.skill(skill);
					}
				}))
			.withTeam(false, t -> t
				.player("marker", p -> p.at(13, 7).stats(6, 3, 3, 5, 8))
				.player("blocker", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private void dodge(GameState state, int roll) {
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("runner", PlayerAction.MOVE));
		TestRolls.on(state).general(roll);
		StepEngine.respond(state, Commands.move("runner", new FieldCoordinate(12, 7), new FieldCoordinate(11, 7)));
	}

	@Test
	public void successfulDodgeDoesNotOfferOptionalModifiers() {
		GameState state = buildState("Break Tackle");
		Game game = state.getGame();

		dodge(state, 5);

		assertNull(game.getDialogParameter());
		assertEquals(new FieldCoordinate(11, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertTrue(game.getActingPlayer().getPlayer().getSkillsIncludingTemporaryOnes().stream()
			.noneMatch(skill -> game.getActingPlayer().isSkillUsed(skill)));
	}

	@Test
	public void failedDodgeOffersTheAvailableModifierCombinations() {
		GameState state = buildState("Break Tackle", "Consummate Professional");
		Game game = state.getGame();

		// agility 3 plus one tacklezone on the target square is a 4+
		dodge(state, 3);

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertEquals(4, parameter.getMinimumRoll());
		assertEquals(3, parameter.getRoll());
		List<String> labels =
			parameter.getModifierOptions().stream().map(option -> option.getLabel()).collect(Collectors.toList());
		assertEquals(2, labels.size());
		assertTrue(labels.contains("Break Tackle"));
		assertTrue(labels.contains("Consummate Professional"));
	}

	@Test
	public void failedDodgeWithoutOptionalModifiersOffersTheReRollDialog() {
		GameState state = buildState();
		Game game = state.getGame();
		game.getTurnData().setReRolls(1);

		dodge(state, 3);

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertTrue(parameter.getModifierOptions().isEmpty());
		assertTrue(parameter.hasProperty(ReRollProperty.TRR));
		assertNull(parameter.getReRollSkill());
	}

	@Test
	public void teamReRollIsNotOfferedWhenTheReRollSkillIsAvailableEveryTurn() {
		GameState state = buildState("Break Tackle", "Dodge");
		Game game = state.getGame();
		game.getTurnData().setReRolls(1);

		dodge(state, 3);

		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertFalse(parameter.hasProperty(ReRollProperty.TRR));
		assertEquals("Dodge", parameter.getReRollSkill().getName());
	}

	@Test
	public void chosenModifierRescuesTheDodgeAndMarksTheSkillUsed() {
		GameState state = buildState("Break Tackle");
		Game game = state.getGame();

		dodge(state, 3);

		Skill breakTackle = game.getRules().getSkillFactory().forName("Break Tackle");
		StepEngine.respond(state, Commands.reRollModifierChoice("runner", ReRolledActions.DODGE, breakTackle));

		assertEquals(new FieldCoordinate(11, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertTrue(game.getActingPlayer().isSkillUsed(breakTackle));
	}
}
