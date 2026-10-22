package com.fumbbl.ffb.test.skill.move;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
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
		return buildState(3, dodgerSkills);
	}

	private GameState buildState(int strength, String... dodgerSkills) {
		return new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t
				.player("runner", p -> {
					p.at(12, 7).stats(6, strength, 3, 5, 8);
					for (String skill : dodgerSkills) {
						p.skill(skill);
					}
				}))
			.withTeam(false, t -> t
				.player("marker", p -> p.at(13, 7).stats(6, 3, 3, 5, 8))
				.player("blocker", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private GameState buildStateWithScoringTeamMate() {
		return new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(24, 3)
			.withTeam(true, t -> t
				.player("runner", p -> {
					p.at(12, 7).stats(6, 3, 3, 5, 8);
					for (String skill : new String[]{"Dodge"}) {
						p.skill(skill);
					}
				})
				.player("carrier", p -> p.at(24, 3).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("marker", p -> p.at(13, 7).stats(6, 3, 3, 5, 8))
				.player("blocker", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private GameState buildStateWithDivingTackler() {
		return new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t
				.player("runner", p -> {
					p.at(12, 7).stats(6, 3, 3, 5, 8);
					for (String skill : new String[]{"Swift As The Breeze"}) {
						p.skill(skill);
					}
				}))
			.withTeam(false, t -> t
				.player("marker", p -> {
					p.at(13, 7).stats(6, 3, 3, 5, 8);
					p.skill("Diving Tackle");
				}))
			.build();
	}

	private GameState buildStateWithDivingTacklerAndMarkedTarget(int strength) {
		return new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t
				.player("runner", p -> {
					p.at(12, 7).stats(6, strength, 3, 5, 8);
					for (String skill : new String[]{"Break Tackle"}) {
						p.skill(skill);
					}
				}))
			.withTeam(false, t -> t
				.player("marker", p -> {
					p.at(13, 7).stats(6, 3, 3, 5, 8);
					p.skill("Diving Tackle");
				})
				.player("blocker", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private List<String> labels(DialogReRollModifierChoiceParameter parameter) {
		return parameter.getModifierOptions().stream().map(ModifierChoiceOption::getLabel).collect(Collectors.toList());
	}

	private Skill swiftAsTheBreeze(Game game) {
		return game.getRules().getSkillFactory().forName("Swift As The Breeze");
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
		// at strength 4 break tackle gives -2, so it is not interchangeable with consummate professional
		GameState state = buildState(4, "Break Tackle", "Consummate Professional");
		Game game = state.getGame();

		// agility 3 plus one tacklezone on the target square is a 4+
		dodge(state, 3);

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertEquals(4, parameter.getMinimumRoll());
		assertEquals(3, parameter.getRoll());
		List<String> labels =
			parameter.getModifierOptions().stream().map(ModifierChoiceOption::getLabel).collect(Collectors.toList());
		assertEquals(2, labels.size());
		assertTrue(labels.contains("Break Tackle"));
		assertTrue(labels.contains("Consummate Professional"));
	}

	@Test
	public void onlyTheSkillUsableMoreOftenIsOfferedWhenTheBonusIsEqual() {
		GameState state = buildState("Break Tackle", "Consummate Professional");
		Game game = state.getGame();

		// at strength 3 both skills give -1, consummate professional is the once per game one
		dodge(state, 3);

		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		List<String> labels =
			parameter.getModifierOptions().stream().map(ModifierChoiceOption::getLabel).collect(Collectors.toList());
		assertEquals(Collections.singletonList("Break Tackle"), labels);
	}

	@Test
	public void modifierOptionsAreOfferedWithoutAnyReRoll() {
		GameState state = buildState("Break Tackle");
		Game game = state.getGame();

		dodge(state, 3);

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertEquals(1, parameter.getModifierOptions().size());
		assertFalse(parameter.hasProperty(ReRollProperty.TRR));
		assertNull(parameter.getReRollSkill());
	}

	@Test
	public void allModifierCombinationsAreListedWithTheirMinimumRoll() {
		GameState state = buildState("Break Tackle", "Consummate Professional");
		Game game = state.getGame();
		game.getTurnData().setReRolls(1);

		// a 1 always fails, so no combination can rescue it, but a re-roll could
		dodge(state, 1);

		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertTrue(parameter.getModifierOptions().isEmpty());
		List<String> combinations = parameter.getModifierCombinations().stream()
			.map(option -> option.getLabel() + ":" + option.getMinimumRoll()).collect(Collectors.toList());
		assertEquals(2, combinations.size());
		assertTrue(combinations.contains("Consummate Professional + Break Tackle:2"));
		assertTrue(combinations.contains("Break Tackle:3"));
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
	public void loneDodgeReRollIsUsedAutomatically() {
		GameState state = buildState("Dodge");
		Game game = state.getGame();

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("runner", PlayerAction.MOVE));
		// the failed dodge is re-rolled right away, the second roll succeeds
		TestRolls.on(state).general(3, 5);
		StepEngine.respond(state, Commands.move("runner", new FieldCoordinate(12, 7), new FieldCoordinate(11, 7)));

		assertNull(game.getDialogParameter());
		assertEquals(new FieldCoordinate(11, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
	}

	@Test
	public void loneDodgeReRollIsOfferedWhenATeamMateWouldBeStalling() {
		GameState state = buildStateWithScoringTeamMate();
		Game game = state.getGame();

		dodge(state, 3);

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertTrue(parameter.getModifierOptions().isEmpty());
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

	@Test
	public void swiftAsTheBreezeIsOfferedAsAModifierOption() {
		GameState state = buildState("Swift As The Breeze", "Consummate Professional");
		Game game = state.getGame();

		// a 3 fails the 4+ dodge but passes the unmodified 3+
		dodge(state, 3);

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertEquals(Arrays.asList("Consummate Professional", "Swift As The Breeze"), labels(parameter));
	}

	@Test
	public void swiftAsTheBreezeIsOfferedTogetherWithTheReRoll() {
		GameState state = buildState("Swift As The Breeze");
		Game game = state.getGame();
		game.getTurnData().setReRolls(1);

		dodge(state, 3);

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertEquals(Collections.singletonList("Swift As The Breeze"), labels(parameter));
		assertTrue(parameter.hasProperty(ReRollProperty.TRR));
	}

	@Test
	public void choosingSwiftAsTheBreezeRescuesTheDodge() {
		GameState state = buildState("Swift As The Breeze");
		Game game = state.getGame();

		dodge(state, 3);

		StepEngine.respond(state, Commands.reRollModifierChoice("runner", ReRolledActions.DODGE, swiftAsTheBreeze(game)));

		assertEquals(new FieldCoordinate(11, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertTrue(game.getActingPlayer().isSkillUsed(swiftAsTheBreeze(game)));
	}

	@Test
	public void swiftAsTheBreezeIsNotAcceptedAsASkillUseCommand() {
		GameState state = buildState("Swift As The Breeze");
		Game game = state.getGame();

		dodge(state, 3);

		StepEngine.respond(state,
			new ClientCommandUseSkill(swiftAsTheBreeze(game), true, "runner", ReRolledActions.DODGE, false));

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		assertFalse(game.getActingPlayer().isSkillUsed(swiftAsTheBreeze(game)));
	}

	@Test
	public void swiftAsTheBreezeIsNotOfferedWhenItCannotRescueTheDodge() {
		GameState state = buildState("Swift As The Breeze");
		Game game = state.getGame();
		game.getTurnData().setReRolls(1);

		// a 2 fails even without the tacklezone
		dodge(state, 2);

		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertTrue(parameter.getModifierOptions().isEmpty());
		StepEngine.respond(state, Commands.reRollModifierChoice("runner", ReRolledActions.DODGE, swiftAsTheBreeze(game)));

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		assertFalse(game.getActingPlayer().isSkillUsed(swiftAsTheBreeze(game)));
	}

	@Test
	public void divingTackleOffersSwiftAsTheBreeze() {
		GameState state = buildStateWithDivingTackler();
		Game game = state.getGame();

		// a 3 passes the plain 3+ dodge, diving tackle pushes the needed roll to 5+
		dodge(state, 3);

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertEquals(Collections.singletonList("Swift As The Breeze"), labels(parameter));
	}

	@Test
	public void divingTackleCannotTripTheDodgerAfterSwiftAsTheBreeze() {
		GameState state = buildStateWithDivingTackler();
		Game game = state.getGame();

		dodge(state, 3);

		StepEngine.respond(state, Commands.reRollModifierChoice("runner", ReRolledActions.DODGE, swiftAsTheBreeze(game)));

		assertEquals(DialogId.PLAYER_CHOICE, game.getDialogParameter().getId());
		DialogPlayerChoiceParameter parameter = (DialogPlayerChoiceParameter) game.getDialogParameter();
		assertEquals(Collections.singletonList("This will NOT trip the dodger, the dodge will still succeed."),
			Arrays.asList(parameter.getDescriptions()));

		StepEngine.respond(state,
			Commands.playerChoice(PlayerChoiceMode.DIVING_TACKLE, game.getPlayerById("marker")));

		assertEquals(new FieldCoordinate(11, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertEquals(PlayerState.PRONE, game.getFieldModel().getPlayerState(game.getPlayerById("marker")).getBase());
	}

	@Test
	public void failedDodgeOptionsIncludeAPendingDivingTackle() {
		// break tackle at strength 5 gives -3
		GameState state = buildStateWithDivingTacklerAndMarkedTarget(5);
		Game game = state.getGame();

		// a 3 fails the 4+ dodge, diving tackle would push it to 6+, break tackle brings it back to 3+
		dodge(state, 3);

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertEquals(Collections.singletonList("Break Tackle"), labels(parameter));
		assertEquals(3, parameter.getModifierOptions().get(0).getMinimumRoll());
		assertEquals(4, parameter.getMinimumRoll());
		assertEquals(Collections.singletonList("Diving Tackle can still be declared, the listed rolls include its modifier."),
			parameter.getMessages());
	}

	@Test
	public void failedDodgeDoesNotOfferAModifierThatAPendingDivingTackleWouldWaste() {
		// break tackle at strength 4 gives -2, enough for the plain dodge but not once diving tackle is declared
		GameState state = buildStateWithDivingTacklerAndMarkedTarget(4);
		Game game = state.getGame();
		game.getTurnData().setReRolls(1);

		dodge(state, 3);

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertTrue(parameter.getModifierOptions().isEmpty());
		assertTrue(parameter.getModifierCombinations().stream().allMatch(option -> option.getMinimumRoll() == 4));
		assertTrue(parameter.hasProperty(ReRollProperty.TRR));
	}

	@Test
	public void choosingAModifierIncludingAPendingDivingTackleSurvivesTheDeclaration() {
		GameState state = buildStateWithDivingTacklerAndMarkedTarget(5);
		Game game = state.getGame();

		dodge(state, 3);

		Skill breakTackle = game.getRules().getSkillFactory().forName("Break Tackle");
		StepEngine.respond(state, Commands.reRollModifierChoice("runner", ReRolledActions.DODGE, breakTackle));

		assertEquals(DialogId.PLAYER_CHOICE, game.getDialogParameter().getId());
		StepEngine.respond(state,
			Commands.playerChoice(PlayerChoiceMode.DIVING_TACKLE, game.getPlayerById("marker")));

		assertEquals(new FieldCoordinate(11, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertEquals(PlayerState.PRONE, game.getFieldModel().getPlayerState(game.getPlayerById("marker")).getBase());
	}
}
