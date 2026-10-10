package com.fumbbl.ffb.test.skill.move;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.Game;
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
 * Covers the BB2025 dialog that lets the coach spend optional modifiers to rescue a failed jump.
 */
public class JumpModifierChoiceTest {

	private static final FieldCoordinate FROM = new FieldCoordinate(12, 7);
	private static final FieldCoordinate TO = new FieldCoordinate(10, 7);

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	private GameState buildState(String... jumperSkills) {
		return new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t
				.player("runner", p -> {
					p.at(FROM.getX(), FROM.getY()).stats(6, 3, 3, 5, 8);
					p.skill("Leap");
					for (String skill : jumperSkills) {
						p.skill(skill);
					}
				}))
			.withTeam(false, t -> t
				.player("marker", p -> p.at(13, 7).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private GameState buildStateWithScoringTeamMate(String... jumperSkills) {
		return new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withBallAt(24, 3)
			.withTeam(true, t -> t
				.player("runner", p -> {
					p.at(FROM.getX(), FROM.getY()).stats(6, 3, 3, 5, 8);
					p.skill("Leap");
					for (String skill : jumperSkills) {
						p.skill(skill);
					}
				})
				.player("carrier", p -> p.at(24, 3).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("marker", p -> p.at(13, 7).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private GameState buildStateWithDivingTackler(String... jumperSkills) {
		return new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withWeather(Weather.NICE)
			.withTeam(true, t -> t
				.player("runner", p -> {
					p.at(FROM.getX(), FROM.getY()).stats(6, 3, 3, 5, 8);
					p.skill("Leap");
					for (String skill : jumperSkills) {
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

	private void jump(GameState state, int roll, int... followUpRolls) {
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("runner", PlayerAction.MOVE));
		StepEngine.respond(state, Commands.selectPlayer("runner", PlayerAction.MOVE, true));
		TestRolls.on(state).general(roll).general(followUpRolls);
		StepEngine.respond(state, Commands.move("runner", FROM, TO));
	}

	private List<String> labels(DialogReRollModifierChoiceParameter parameter) {
		return parameter.getModifierOptions().stream().map(option -> option.getLabel()).collect(Collectors.toList());
	}

	@Test
	public void successfulJumpDoesNotOfferOptionalModifiers() {
		GameState state = buildState("Consummate Professional");
		Game game = state.getGame();

		// agility 3+ with one tacklezone needs a 4+
		jump(state, 5);

		assertNull(game.getDialogParameter());
		assertEquals(TO, game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertTrue(game.getActingPlayer().getPlayer().getSkillsIncludingTemporaryOnes().stream()
			.noneMatch(skill -> game.getActingPlayer().isSkillUsed(skill)));
	}

	@Test
	public void failedJumpOffersTheAvailableModifierOptions() {
		GameState state = buildState("Consummate Professional");
		Game game = state.getGame();

		jump(state, 3);

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertEquals(4, parameter.getMinimumRoll());
		assertEquals(3, parameter.getRoll());
		assertEquals(Collections.singletonList("Consummate Professional"), labels(parameter));
	}

	@Test
	public void chosenModifierRescuesTheJumpAndMarksTheSkillUsed() {
		GameState state = buildState("Consummate Professional");
		Game game = state.getGame();

		jump(state, 3);

		Skill consummateProfessional = game.getRules().getSkillFactory().forName("Consummate Professional");
		StepEngine.respond(state,
			Commands.reRollModifierChoice("runner", ReRolledActions.JUMP, consummateProfessional));

		assertEquals(TO, game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertTrue(game.getActingPlayer().isSkillUsed(consummateProfessional));
	}

	@Test
	public void freeModifiersApplyAutomaticallyAndAreNotOffered() {
		GameState state = buildState("Very Long Legs");
		Game game = state.getGame();

		// very long legs cancels the tacklezone, so a 3 is enough and nothing has to be picked
		jump(state, 3);

		assertNull(game.getDialogParameter());
		assertEquals(TO, game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
	}

	@Test
	public void freeModifiersAreOfferedWhenATeamMateWouldBeStalling() {
		GameState state = buildStateWithScoringTeamMate("Very Long Legs");
		Game game = state.getGame();

		// the coach may want to fail the jump, so very long legs has to be confirmed explicitly
		jump(state, 3);

		assertEquals(DialogId.SKILL_USE, game.getDialogParameter().getId());
		DialogSkillUseParameter parameter = (DialogSkillUseParameter) game.getDialogParameter();
		assertEquals("Very Long Legs", parameter.getSkill().getName());
		assertEquals(Collections.singletonList(
				"You are only asked because failing the jump would end the turn and skip the stalling roll."),
			parameter.getMessages());
	}

	@Test
	public void usingTheFreeModifierRescuesTheJumpWithoutSpendingTheSkill() {
		GameState state = buildStateWithScoringTeamMate("Very Long Legs");
		Game game = state.getGame();

		jump(state, 3);

		Skill veryLongLegs = game.getRules().getSkillFactory().forName("Very Long Legs");
		StepEngine.respond(state, new ClientCommandUseSkill(veryLongLegs, true, "runner", null, false));

		assertEquals(TO, game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertFalse(game.getActingPlayer().isSkillUsed(veryLongLegs));
	}

	@Test
	public void decliningTheFreeModifierLeavesTheJumpFailed() {
		GameState state = buildStateWithScoringTeamMate("Very Long Legs");
		Game game = state.getGame();

		jump(state, 3);

		Skill veryLongLegs = game.getRules().getSkillFactory().forName("Very Long Legs");
		TestRolls.on(state).armor(1, 1);
		StepEngine.respond(state, new ClientCommandUseSkill(veryLongLegs, false, "runner", null, false));

		// the jumper drops in the square they jumped to, which ends the turn
		assertNull(game.getDialogParameter());
		assertEquals(PlayerState.PRONE,
			game.getFieldModel().getPlayerState(game.getPlayerById("runner")).getBase());
	}

	@Test
	public void decliningTheFreeModifierSuppressesModifiersAndReRolls() {
		GameState state = buildStateWithScoringTeamMate("Very Long Legs", "Consummate Professional");
		Game game = state.getGame();
		game.getTurnData().setReRolls(1);

		jump(state, 3);

		Skill veryLongLegs = game.getRules().getSkillFactory().forName("Very Long Legs");
		TestRolls.on(state).armor(1, 1);
		StepEngine.respond(state, new ClientCommandUseSkill(veryLongLegs, false, "runner", null, false));

		// declining a free modifier is a deliberate failure, so nothing is offered to rescue the jump
		assertNull(game.getDialogParameter());
		assertEquals(PlayerState.PRONE,
			game.getFieldModel().getPlayerState(game.getPlayerById("runner")).getBase());
	}

	@Test
	public void freeModifiersAreNotOfferedWhenTheyCannotRescueTheJump() {
		GameState state = buildStateWithScoringTeamMate("Very Long Legs");
		Game game = state.getGame();

		// very long legs would only bring the needed roll down to 3+, so a 2 fails either way
		jump(state, 2, 1, 1);

		assertNull(game.getDialogParameter());
		assertEquals(PlayerState.PRONE,
			game.getFieldModel().getPlayerState(game.getPlayerById("runner")).getBase());
	}

	@Test
	public void divingTackleIsOfferedEvenWhenTheJumpWouldStillSucceed() {
		GameState state = buildStateWithDivingTackler();
		Game game = state.getGame();

		// a 6 passes the jump even with the diving tackle modifier
		jump(state, 6);

		assertEquals(DialogId.PLAYER_CHOICE, game.getDialogParameter().getId());
		DialogPlayerChoiceParameter parameter = (DialogPlayerChoiceParameter) game.getDialogParameter();
		assertEquals(PlayerChoiceMode.DIVING_TACKLE, parameter.getPlayerChoiceMode());
		assertEquals(Collections.singletonList("marker"), Arrays.asList(parameter.getPlayerIds()));
		assertEquals(Collections.singletonList("This will NOT trip the jumper, the jump will still succeed."),
			Arrays.asList(parameter.getDescriptions()));
	}

	@Test
	public void decliningTheOfferedDivingTackleLeavesTheJumpSuccessful() {
		GameState state = buildStateWithDivingTackler();
		Game game = state.getGame();

		jump(state, 6);
		StepEngine.respond(state, Commands.playerChoice(PlayerChoiceMode.DIVING_TACKLE));

		assertNull(game.getDialogParameter());
		assertEquals(TO, game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
	}

	@Test
	public void usingTheOfferedDivingTackleStillLeavesTheJumpSuccessful() {
		GameState state = buildStateWithDivingTackler();
		Game game = state.getGame();

		jump(state, 6);
		StepEngine.respond(state,
			Commands.playerChoice(PlayerChoiceMode.DIVING_TACKLE, game.getPlayerById("marker")));

		assertEquals(TO, game.getFieldModel().getPlayerCoordinate(game.getPlayerById("runner")));
		assertEquals(PlayerState.PRONE,
			game.getFieldModel().getPlayerState(game.getPlayerById("marker")).getBase());
	}

	@Test
	public void divingTackleIsOfferedWithoutRescueWhenItTripsTheJumper() {
		GameState state = buildStateWithDivingTackler();
		Game game = state.getGame();

		// a 4 passes the plain jump, diving tackle pushes the needed roll to 5+
		jump(state, 4);

		assertEquals(DialogId.PLAYER_CHOICE, game.getDialogParameter().getId());
		DialogPlayerChoiceParameter parameter = (DialogPlayerChoiceParameter) game.getDialogParameter();
		assertEquals(Collections.singletonList("This will trip the jumper."),
			Arrays.asList(parameter.getDescriptions()));
	}

	@Test
	public void divingTackleOffersTheModifierOptionsBeforeTheOpposingCoachDecides() {
		GameState state = buildStateWithDivingTackler("Consummate Professional");
		Game game = state.getGame();

		// a 4 passes the plain jump, diving tackle pushes the needed roll to 5+
		jump(state, 4);

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, game.getDialogParameter().getId());
		DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		assertEquals(Collections.singletonList("Consummate Professional"), labels(parameter));
	}

	@Test
	public void divingTackleDialogNamesTheModifiersItWouldForce() {
		GameState state = buildStateWithDivingTackler("Consummate Professional");
		Game game = state.getGame();

		jump(state, 4);

		Skill consummateProfessional = game.getRules().getSkillFactory().forName("Consummate Professional");
		StepEngine.respond(state,
			Commands.reRollModifierChoice("runner", ReRolledActions.JUMP, consummateProfessional));

		assertEquals(DialogId.PLAYER_CHOICE, game.getDialogParameter().getId());
		DialogPlayerChoiceParameter parameter = (DialogPlayerChoiceParameter) game.getDialogParameter();
		assertEquals(Collections.singletonList(
				"This will NOT trip the jumper, but will force the use of Consummate Professional."),
			Arrays.asList(parameter.getDescriptions()));
	}
}
