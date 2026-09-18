package com.fumbbl.ffb.test.skill.move;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.bb2025.move.StepPickUp;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import com.fumbbl.ffb.util.UtilPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PickupModifierChoiceTest {
	private TestServer server;

	@BeforeEach
	void setUp() throws Exception {
		server = new TestServer();
	}

	private GameState buildState(String... skills) {
		return new GameStateBuilder(server.getGameState())
			.withRule("BB2025").withWeather(Weather.NICE).withBallAt(11, 7)
			.withTeam(true, team -> team.player("runner", player -> {
				player.at(12, 7).stats(6, 3, 3, 5, 8);
				for (String skill : skills) {
					player.skill(skill);
				}
			}))
			.withTeam(false, team -> team.player("opponent", player -> player.at(5, 5).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private IStep pickup(GameState state, int... rolls) {
		state.getGame().getFieldModel().setBallMoving(true);
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("runner", PlayerAction.MOVE));
		TestRolls.on(state).general(rolls);
		return StepEngine.respond(state, Commands.move("runner", new FieldCoordinate(12, 7), new FieldCoordinate(11, 7)));
	}

	private Skill professional(GameState state) {
		return state.getGame().getRules().getSkillFactory().forName("Consummate Professional");
	}

	private DialogReRollModifierChoiceParameter dialog(GameState state) {
		return (DialogReRollModifierChoiceParameter) state.getGame().getDialogParameter();
	}

	private void chooseModifier(GameState state) {
		StepEngine.respond(state, Commands.reRollModifierChoice("runner", ReRolledActions.PICK_UP, professional(state)));
	}

	@Test
	void successfulPickupDoesNotSpendOrOfferModifier() {
		GameState state = buildState("Consummate Professional");
		pickup(state, 3);
		assertNull(state.getGame().getDialogParameter());
		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("runner")));
		assertFalse(state.getGame().getActingPlayer().isSkillUsed(professional(state)));
	}

	@Test
	void failedPickupOffersModifierAndChoosingItRescuesOriginalDie() {
		GameState state = buildState("Consummate Professional");
		assertEquals(StepId.PICK_UP, pickup(state, 2).getId());
		DialogReRollModifierChoiceParameter dialog = dialog(state);
		assertEquals(ReRolledActions.PICK_UP, dialog.getReRolledAction());
		assertEquals(3, dialog.getMinimumRoll());
		assertEquals(2, dialog.getRoll());
		assertEquals("Consummate Professional", dialog.getModifierOptions().get(0).getLabel());
		assertEquals(2, dialog.getModifierOptions().get(0).getMinimumRoll());
		assertNull(dialog.getReRollSkill());
		assertFalse(dialog.hasProperty(ReRollProperty.TRR));

		chooseModifier(state);

		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("runner")));
		assertTrue(state.getGame().getActingPlayer().isSkillUsed(professional(state)));
		assertTrue(state.getGame().getPlayerById("runner").isUsed(professional(state)));
	}

	@Test
	void actionableModifierIsOfferedAlongsideSureHands() {
		GameState state = buildState("Consummate Professional", "Sure Hands");
		pickup(state, 2);
		assertEquals("Sure Hands", dialog(state).getReRollSkill().getName());
		assertEquals(1, dialog(state).getModifierOptions().size());
		chooseModifier(state);
		assertFalse(state.getGame().getActingPlayer().isSkillUsed(
			state.getGame().getRules().getSkillFactory().forName("Sure Hands")));
	}

	@Test
	void sureHandsStillRerollsAutomaticallyWithoutActionableModifier() {
		GameState state = buildState("Sure Hands");
		pickup(state, 2, 3);
		assertNull(state.getGame().getDialogParameter());
		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("runner")));
	}

	@Test
	void naturalOneUsesSureHandsAutomaticallyThenOffersModifierOnRerolledDie() {
		GameState state = buildState("Consummate Professional", "Sure Hands");
		pickup(state, 1, 2);
		assertEquals(2, dialog(state).getRoll());
		assertEquals(1, dialog(state).getModifierOptions().size());
		assertNull(dialog(state).getReRollSkill());
		assertFalse(dialog(state).hasProperty(ReRollProperty.TRR));
		chooseModifier(state);
		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("runner")));
	}

	@Test
	void coachCanChooseSureHandsInsteadAndSpendModifierAfterReroll() {
		GameState state = buildState("Consummate Professional", "Sure Hands");
		pickup(state, 2);
		TestRolls.on(state).general(2);
		Skill sureHands = dialog(state).getReRollSkill();
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.PICK_UP,
			sureHands.getRerollSource(ReRolledActions.PICK_UP)));
		assertEquals(1, dialog(state).getModifierOptions().size());
		assertNull(dialog(state).getReRollSkill());
		chooseModifier(state);
		assertTrue(state.getGame().getActingPlayer().isSkillUsed(professional(state)));
	}

	@Test
	void teamRerollCanBeFollowedByModifierSelection() {
		GameState state = buildState("Consummate Professional");
		state.getGame().getTurnData().setReRolls(1);
		pickup(state, 1);
		assertTrue(dialog(state).getModifierOptions().isEmpty());
		assertEquals(1, dialog(state).getModifierCombinations().size());
		assertTrue(dialog(state).hasProperty(ReRollProperty.TRR));
		TestRolls.on(state).general(2);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.PICK_UP, ReRollSources.TEAM_RE_ROLL));
		assertFalse(dialog(state).hasProperty(ReRollProperty.TRR));
		chooseModifier(state);
		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("runner")));
	}

	@Test
	void decliningModifierFailsPickupWithoutSpendingIt() {
		GameState state = buildState("Consummate Professional", "Sure Hands");
		pickup(state, 2);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.PICK_UP, null));
		assertFalse(state.getGame().getPlayerById("runner").isUsed(professional(state)));
		assertFalse(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("runner")));
		assertFalse(state.getGame().isHomePlaying());
	}

	@Test
	void usedProfessionalCannotRescueAnotherPickup() {
		GameState state = buildState("Consummate Professional");
		state.getGame().getPlayerById("runner").markUsed(professional(state), state.getGame());
		pickup(state, 2);
		assertFalse(state.getGame().isHomePlaying());
		assertFalse(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("runner")));
	}

	@Test
	void naturalOneCannotBeRescuedWithoutReroll() {
		GameState state = buildState("Consummate Professional");
		pickup(state, 1);
		assertFalse(state.getGame().isHomePlaying());
		assertFalse(state.getGame().getPlayerById("runner").isUsed(professional(state)));
	}

	@Test
	void invalidModifierChoiceDoesNotSpendSkillOrChangeRoll() {
		GameState state = buildState("Consummate Professional", "Sure Hands");
		pickup(state, 2);
		Game game = state.getGame();
		Skill sureHands = game.getRules().getSkillFactory().forName("Sure Hands");
		StepEngine.respond(state, Commands.reRollModifierChoice("runner", ReRolledActions.PICK_UP, sureHands));
		assertEquals(2, dialog(state).getRoll());
		assertFalse(game.getActingPlayer().isSkillUsed(sureHands));
		chooseModifier(state);
		assertTrue(UtilPlayer.hasBall(game, game.getPlayerById("runner")));
	}

	@Test
	void savedStepAndDialogResumeModifierSelectionWithoutAnotherDie() {
		GameState state = buildState("Consummate Professional", "Sure Hands");
		pickup(state, 2);
		StepPickUp step = (StepPickUp) state.getCurrentStep();
		JsonObject saved = step.toJsonValue();
		StepPickUp restored = new StepPickUp(state).initFrom(state.getGame().getRules(), saved);
		assertEquals(saved, restored.toJsonValue());
		step.initFrom(state.getGame().getRules(), restored.toJsonValue());
		DialogReRollModifierChoiceParameter restoredDialog = new DialogReRollModifierChoiceParameter();
		restoredDialog.initFrom(state.getGame().getRules(), dialog(state).toJsonValue());
		state.getGame().setDialogParameter(restoredDialog);

		chooseModifier(state);

		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("runner")));
		JsonObject committed = step.toJsonValue();
		assertEquals(1, IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.getFrom(state.getGame().getRules(), committed).size());
		assertEquals(committed, new StepPickUp(state).initFrom(state.getGame().getRules(), committed).toJsonValue());
	}

	@Test
	void savedStepAfterRerollCannotOfferAnotherReroll() {
		GameState state = buildState("Consummate Professional", "Sure Hands");
		state.getGame().getTurnData().setReRolls(1);
		pickup(state, 1, 2);
		StepPickUp step = (StepPickUp) state.getCurrentStep();
		JsonObject saved = step.toJsonValue();
		assertTrue(IServerJsonOption.RE_ROLL_USED.getFrom(state.getGame().getRules(), saved));
		step.initFrom(state.getGame().getRules(), saved);
		chooseModifier(state);
		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("runner")));
		assertEquals(1, state.getGame().getTurnData().getReRolls());
	}

	@Test
	void legacyPendingStepWithoutModifierFieldsCanStillReroll() {
		GameState state = buildState("Consummate Professional");
		state.getGame().getTurnData().setReRolls(1);
		pickup(state, 1);
		StepPickUp step = (StepPickUp) state.getCurrentStep();
		JsonObject legacy = step.toJsonValue();
		legacy.remove("pickupRoll");
		legacy.remove("reRollUsed");
		legacy.remove("awaitingRescue");
		legacy.remove("selectedAgilityModifierSkills");
		step.initFrom(state.getGame().getRules(), legacy);
		TestRolls.on(state).general(3);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.PICK_UP, ReRollSources.TEAM_RE_ROLL));
		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("runner")));
		assertFalse(state.getGame().getPlayerById("runner").isUsed(professional(state)));
	}

	@Test
	void naturalOneOnSureHandsRerollDoesNotSpendModifierOrAllowThirdRoll() {
		GameState state = buildState("Consummate Professional", "Sure Hands");
		state.getGame().getTurnData().setReRolls(1);
		pickup(state, 1, 1);
		assertFalse(state.getGame().isHomePlaying());
		assertFalse(state.getGame().getPlayerById("runner").isUsed(professional(state)));
		assertNull(state.getGame().getDialogParameter());
	}

	@Test
	void decliningAfterRerollFailsWithoutUsingProfessional() {
		GameState state = buildState("Consummate Professional", "Sure Hands");
		pickup(state, 1, 2);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.PICK_UP, null));
		assertFalse(state.getGame().isHomePlaying());
		assertFalse(state.getGame().getPlayerById("runner").isUsed(professional(state)));
	}

	@Test
	void successfulSureHandsRerollKeepsProfessionalUnused() {
		GameState state = buildState("Consummate Professional", "Sure Hands");
		pickup(state, 2);
		Skill sureHands = dialog(state).getReRollSkill();
		TestRolls.on(state).general(3);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.PICK_UP,
			sureHands.getRerollSource(ReRolledActions.PICK_UP)));
		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("runner")));
		assertFalse(state.getGame().getPlayerById("runner").isUsed(professional(state)));
	}

	@Test
	void mismatchedOrDuplicateModifierSelectionsAreRejected() {
		GameState state = buildState("Consummate Professional");
		pickup(state, 2);
		Skill skill = professional(state);
		StepEngine.respond(state, Commands.reRollModifierChoice("opponent", ReRolledActions.PICK_UP, skill));
		StepEngine.respond(state, Commands.reRollModifierChoice("runner", ReRolledActions.DODGE, skill));
		StepEngine.respond(state, Commands.reRollModifierChoice("runner", ReRolledActions.PICK_UP, skill, skill));
		StepEngine.respond(state, Commands.reRollModifierChoice("runner", ReRolledActions.PICK_UP));
		assertEquals(2, dialog(state).getRoll());
		assertFalse(state.getGame().getPlayerById("runner").isUsed(skill));
		chooseModifier(state);
		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("runner")));
	}

	@Test
	void secureTheBallDoesNotOfferAgilityModifier() {
		GameState state = buildState("Consummate Professional");
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("runner", PlayerAction.MOVE));
		Game game = state.getGame();
		game.getActingPlayer().setPlayerAction(PlayerAction.SECURE_THE_BALL);
		game.getFieldModel().setWeather(Weather.POURING_RAIN);
		game.getFieldModel().setBallCoordinate(new FieldCoordinate(12, 7));
		game.getFieldModel().setBallMoving(true);
		StepPickUp step = new StepPickUp(state);
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(new StepParameter(StepParameterKey.GOTO_LABEL_ON_FAILURE, "failure"));
		step.init(parameters);
		TestRolls.on(state).general(2);
		step.start();
		assertEquals(StepAction.GOTO_LABEL, step.getResult().getNextAction());
		assertNull(game.getDialogParameter());
		assertFalse(game.getPlayerById("runner").isUsed(professional(state)));
	}

	@Test
	void overridePlayerSpendsTheirOwnProfessionalRatherThanActingPlayers() {
		GameState state = new GameStateBuilder(server.getGameState())
			.withRule("BB2025").withWeather(Weather.NICE).withBallAt(11, 7)
			.withTeam(true, team -> team
				.player("runner", player -> player.at(12, 7).stats(6, 3, 3, 5, 8).skill("Consummate Professional"))
				.player("receiver", player -> player.at(11, 7).stats(6, 3, 3, 5, 8).skill("Consummate Professional")))
			.withTeam(false, team -> team.player("opponent", player -> player.at(5, 5).stats(6, 3, 3, 5, 8)))
			.build();
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("runner", PlayerAction.MOVE));
		Game game = state.getGame();
		game.getFieldModel().setBallMoving(true);
		StepPickUp step = new StepPickUp(state);
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(new StepParameter(StepParameterKey.GOTO_LABEL_ON_FAILURE, "failure"));
		step.init(parameters);
		step.setParameter(new StepParameter(StepParameterKey.PLAYER_ON_BALL_ID, "receiver"));
		TestRolls.on(state).general(2);
		step.start();
		assertEquals("receiver", dialog(state).getPlayerId());
		step.handleCommand(new ReceivedCommand(
			Commands.reRollModifierChoice("receiver", ReRolledActions.PICK_UP, professional(state)), null));
		assertTrue(game.getPlayerById("receiver").isUsed(professional(state)));
		assertFalse(game.getActingPlayer().isSkillUsed(professional(state)));
		assertTrue(UtilPlayer.hasBall(game, game.getPlayerById("receiver")));
	}
}
