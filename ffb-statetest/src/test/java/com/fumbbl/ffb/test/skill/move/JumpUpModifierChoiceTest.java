package com.fumbbl.ffb.test.skill.move;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.factory.JumpUpModifierFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.JumpUpContext;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.net.commands.ServerCommandModelSync;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportJumpUpRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.bb2025.action.select.StepJumpUp;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JumpUpModifierChoiceTest {
	private TestServer server;

	@BeforeEach
	void setUp() throws Exception {
		server = new TestServer();
	}

	private GameState buildState(String rules, int agility, String... skills) {
		return new GameStateBuilder(server.getGameState()).withRule(rules)
			.withTeam(true, team -> team.player("jumper", player -> {
				player.at(7, 7).stats(6, 3, agility, 5, 8).skill("Jump Up")
					.state(new PlayerState(PlayerState.PRONE).changeActive(true));
				for (String skill : skills) {
					player.skill(skill);
				}
			}).player("teammate", player -> player.at(10, 7).stats(6, 3, 3, 5, 8)))
			.withTeam(false, team -> team.player("opponent", player -> player.at(8, 7).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private GameState buildState() {
		return buildState("BB2025", 4, "Consummate Professional");
	}

	private IStep jumpUp(GameState state, int roll) {
		return jumpUpWithRolls(state, roll, 5);
	}

	private IStep jumpUpWithRolls(GameState state, int... rolls) {
		StepEngine.start(state);
		TestRolls.on(state).general(rolls).block("pushback");
		StepEngine.respond(state, Commands.selectPlayer("jumper", PlayerAction.BLOCK));
		return StepEngine.respond(state, Commands.block("jumper", "opponent"));
	}

	private Skill skill(GameState state, String name) {
		return state.getGame().getRules().getSkillFactory().forName(name);
	}

	private Skill professional(GameState state) {
		return skill(state, "Consummate Professional");
	}

	private DialogReRollModifierChoiceParameter dialog(GameState state) {
		return (DialogReRollModifierChoiceParameter) state.getGame().getDialogParameter();
	}

	private void chooseModifier(GameState state) {
		StepEngine.respond(state, Commands.reRollModifierChoice("jumper", ReRolledActions.JUMP_UP, professional(state)));
	}

	private PlayerState playerState(GameState state) {
		return state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("jumper"));
	}

	private void assertFailedWithoutTurnover(GameState state) {
		assertEquals(PlayerState.PRONE, playerState(state).getBase());
		assertFalse(playerState(state).isActive());
		assertTrue(state.getGame().isHomePlaying());
		assertNull(state.getGame().getActingPlayer().getPlayer());
		assertNull(state.getGame().getDialogParameter());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void failedJumpUpOffersOnlyRescuingModifierAndSpendsItWithoutAnotherRoll() {
		GameState state = buildState();
		assertEquals(StepId.JUMP_UP, jumpUp(state, 2).getId());
		assertEquals(ReRolledActions.JUMP_UP, dialog(state).getReRolledAction());
		assertEquals("jumper", dialog(state).getPlayerId());
		assertEquals(3, dialog(state).getMinimumRoll());
		assertEquals(2, dialog(state).getRoll());
		assertEquals(1, dialog(state).getModifierOptions().size());
		assertEquals(2, dialog(state).getModifierOptions().get(0).getMinimumRoll());
		assertEquals(-1, dialog(state).getModifierOptions().get(0).getTotalModifier());
		assertFalse(state.getGame().getPlayerById("jumper").isUsed(professional(state)));

		chooseModifier(state);

		assertEquals(PlayerState.MOVING, playerState(state).getBase());
		assertFalse(state.getGame().getActingPlayer().isStandingUp());
		assertTrue(state.getGame().getActingPlayer().hasMoved());
		assertTrue(state.getGame().getActingPlayer().isSkillUsed(professional(state)));
		assertTrue(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertTrue(state.getGame().isHomePlaying());
		assertEquals(StepId.BLOCK_ROLL, state.getCurrentStep().getId());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void successfulRollDoesNotOfferOrSpendModifier() {
		assertSuccessfulRoll(3);
	}

	@Test
	void naturalSixDoesNotOfferOrSpendModifier() {
		assertSuccessfulRoll(6);
	}

	private void assertSuccessfulRoll(int roll) {
		GameState state = buildState();
		jumpUp(state, roll);
		assertEquals(StepId.BLOCK_ROLL, state.getCurrentStep().getId());
		assertEquals(PlayerState.MOVING, playerState(state).getBase());
		assertFalse(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void naturalOneFailsWithoutSpendingModifier() {
		GameState state = buildState();
		jumpUp(state, 1);
		assertFalse(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertFailedWithoutTurnover(state);
	}

	@Test
	void insufficientModifierIsNotOffered() {
		GameState state = buildState("BB2025", 5, "Consummate Professional");
		jumpUp(state, 2);
		assertFalse(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertFailedWithoutTurnover(state);
	}

	@Test
	void decliningLeavesPlayerProneAndOnlyEndsTheirActivation() {
		GameState state = buildState();
		StepJumpUp step = (StepJumpUp) jumpUp(state, 2);
		assertNull(step.getReRolledAction());
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, null));
		assertEquals(ReRolledActions.JUMP_UP, step.getReRolledAction());
		assertNull(step.getReRollSource());
		assertEquals(StepAction.GOTO_LABEL, step.getResult().getNextAction());
		assertEquals(2, IServerJsonOption.JUMP_UP_ROLL.getFrom(state.getGame().getRules(), step.toJsonValue()));
		assertFalse(IServerJsonOption.AWAITING_RESCUE.getFrom(state.getGame().getRules(), step.toJsonValue()));
		assertFalse(step.getResult().getReportList().hasReport(ReportId.JUMP_UP_ROLL));
		assertFalse(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertFailedWithoutTurnover(state);
		StepEngine.respond(state, Commands.selectPlayer("teammate", PlayerAction.MOVE));
		assertEquals("teammate", state.getGame().getActingPlayer().getPlayerId());
	}

	@Test
	void usedProfessionalCannotRescueJumpUp() {
		GameState state = buildState();
		state.getGame().getPlayerById("jumper").markUsed(professional(state), state.getGame());
		jumpUp(state, 2);
		assertFailedWithoutTurnover(state);
	}

	@Test
	void noOptionalSkillMeansNoPrompt() {
		GameState state = buildState("BB2025", 4);
		jumpUp(state, 2);
		assertFailedWithoutTurnover(state);
	}

	@Test
	void teamAndProAppearAlongsideModifierButChoosingModifierSpendsNeither() {
		GameState state = buildState("BB2025", 4, "Consummate Professional", "Pro");
		state.getGame().getTurnData().setReRolls(2);
		jumpUp(state, 2);
		assertNull(dialog(state).getReRollSkill());
		assertTrue(dialog(state).hasProperty(ReRollProperty.TRR));
		assertTrue(dialog(state).hasProperty(ReRollProperty.PRO));
		chooseModifier(state);
		assertEquals(2, state.getGame().getTurnData().getReRolls());
		assertFalse(playerState(state).hasUsedPro());
	}

	@Test
	void failureWithoutRescuingModifierStillOffersTeamReroll() {
		GameState state = buildState();
		state.getGame().getTurnData().setReRolls(2);
		jumpUp(state, 1);
		assertTrue(dialog(state).getModifierOptions().isEmpty());
		assertTrue(dialog(state).hasProperty(ReRollProperty.TRR));
		assertFalse(dialog(state).hasProperty(ReRollProperty.PRO));
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, null));
		assertEquals(2, state.getGame().getTurnData().getReRolls());
		assertFailedWithoutTurnover(state);
	}

	@Test
	void invalidAndDuplicateCommandsCannotSpendSkillsOrRollDice() {
		GameState state = buildState("BB2025", 4, "Consummate Professional", "Pro");
		state.getGame().getTurnData().setReRolls(2);
		jumpUp(state, 2);
		StepJumpUp step = (StepJumpUp) state.getCurrentStep();
		JsonObject saved = step.toJsonValue();
		Skill professional = professional(state);
		NetCommand[] invalid = {
			Commands.reRollModifierChoice("opponent", ReRolledActions.JUMP_UP, professional),
			Commands.reRollModifierChoice("jumper", ReRolledActions.DODGE, professional),
			Commands.reRollModifierChoice("jumper", ReRolledActions.JUMP_UP),
			Commands.reRollModifierChoice("jumper", ReRolledActions.JUMP_UP, professional, professional),
			Commands.reRollModifierChoice("jumper", ReRolledActions.JUMP_UP, skill(state, "Pro")),
			new ClientCommandUseSkill(professional, true, "jumper", ReRolledActions.JUMP_UP, false)
		};
		for (NetCommand command : invalid) {
			assertEquals(StepCommandStatus.UNHANDLED_COMMAND, step.handleCommand(new ReceivedCommand(command, null)));
			assertEquals(saved, step.toJsonValue());
		}
		chooseModifier(state);
		assertEquals(StepCommandStatus.UNHANDLED_COMMAND, step.handleCommand(new ReceivedCommand(
			Commands.reRollModifierChoice("jumper", ReRolledActions.JUMP_UP, professional), null)));
		assertEquals(5, state.getDiceRoller().rollSkill());
		assertEquals(2, state.getGame().getTurnData().getReRolls());
	}

	@Test
	void selectionRechecksUsageBeforeSpendingSkill() {
		GameState state = buildState();
		jumpUp(state, 2);
		state.getGame().getActingPlayer().markSkillUsed(professional(state));
		StepJumpUp step = (StepJumpUp) state.getCurrentStep();
		assertEquals(StepCommandStatus.UNHANDLED_COMMAND, step.handleCommand(new ReceivedCommand(
			Commands.reRollModifierChoice("jumper", ReRolledActions.JUMP_UP, professional(state)), null)));
		assertEquals(2, dialog(state).getRoll());
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, null));
		assertFailedWithoutTurnover(state);
	}

	@Test
	void savedStepAndDialogResumeOriginalRollAndPersistSelection() {
		GameState state = buildState();
		jumpUp(state, 2);
		StepJumpUp step = (StepJumpUp) state.getCurrentStep();
		JsonObject saved = step.toJsonValue();
		IStep restored = state.getStepFactory().forJsonValue(state.getGame().getRules(), saved);
		assertEquals(StepJumpUp.class, restored.getClass());
		assertEquals(saved, restored.toJsonValue());
		step.initFrom(state.getGame().getRules(), restored.toJsonValue());
		DialogReRollModifierChoiceParameter restoredDialog = new DialogReRollModifierChoiceParameter();
		restoredDialog.initFrom(state.getGame().getRules(), dialog(state).toJsonValue());
		state.getGame().setDialogParameter(restoredDialog);
		step.start();
		assertEquals(2, dialog(state).getRoll());
		chooseModifier(state);
		JsonObject committed = step.toJsonValue();
		assertEquals(1, IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.getFrom(state.getGame().getRules(), committed).size());
		StepJumpUp restoredSelection = new StepJumpUp(state).initFrom(state.getGame().getRules(), committed);
		assertEquals(committed, restoredSelection.toJsonValue());
		restoredSelection.getResult().reset();
		restoredSelection.start();
		assertEquals(StepAction.NEXT_STEP, restoredSelection.getResult().getNextAction());
		assertNull(state.getGame().getDialogParameter());
		assertEquals(0, restoredSelection.getResult().getReportList().size());
		assertEquals(5, state.getDiceRoller().rollSkill());
		assertTrue(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
	}

	@Test
	void unstartedStepRoundTripsAndUsesCurrentSelectionFlow() {
		GameState state = buildState();
		Game game = state.getGame();
		game.getActingPlayer().setPlayerId("jumper");
		game.getActingPlayer().setPlayerAction(PlayerAction.BLOCK);
		game.getActingPlayer().setStandingUp(true);
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(StepParameter.from(StepParameterKey.GOTO_LABEL_ON_FAILURE, "failure"));
		IStep original = new StepJumpUp(state);
		original.init(parameters);
		IStep restored = state.getStepFactory().forJsonValue(game.getRules(), original.toJsonValue());
		assertEquals(original.toJsonValue(), restored.toJsonValue());
		TestRolls.on(state).general(2, 5);
		restored.start();
		assertEquals(StepJumpUp.class, restored.getClass());
		assertEquals(2, dialog(state).getRoll());
		assertEquals(StepCommandStatus.EXECUTE_STEP, restored.handleCommand(new ReceivedCommand(
			Commands.reRollModifierChoice("jumper", ReRolledActions.JUMP_UP, professional(state)), null)));
		assertEquals(StepAction.NEXT_STEP, restored.getResult().getNextAction());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void ordinaryMoveDoesNotRollOrOfferModifier() {
		GameState state = buildState();
		StepEngine.start(state);
		TestRolls.on(state).general(5);
		StepEngine.respond(state, Commands.selectPlayer("jumper", PlayerAction.MOVE));
		assertNull(state.getGame().getDialogParameter());
		assertFalse(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void savedPendingChoiceCanBeDeclinedWithoutRollingOrSpendingResources() {
		GameState state = buildState();
		state.getGame().getTurnData().setReRolls(1);
		StepJumpUp step = (StepJumpUp) jumpUp(state, 2);
		step.initFrom(state.getGame().getRules(), step.toJsonValue());
		step.start();
		assertEquals(2, dialog(state).getRoll());
		step.getResult().reset();
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, null));
		assertEquals(ReRolledActions.JUMP_UP, step.getReRolledAction());
		assertFalse(step.getResult().getReportList().hasReport(ReportId.JUMP_UP_ROLL));
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		assertFalse(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertFailedWithoutTurnover(state);
	}

	@Test
	void superclassRerollDispatchSpendsTeamResourceAndReportsSuccessfulReroll() {
		GameState state = buildState();
		state.getGame().getTurnData().setReRolls(1);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 2, 3, 5);
		assertJumpUpReport(step, 2, false, false);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.TEAM_RE_ROLL));
		assertEquals(ReRolledActions.JUMP_UP, step.getReRolledAction());
		assertEquals(ReRollSources.TEAM_RE_ROLL, step.getReRollSource());
		assertEquals(StepAction.NEXT_STEP, step.getResult().getNextAction());
		assertEquals(0, state.getGame().getTurnData().getReRolls());
		assertFalse(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertJumpUpReport(step, 3, true, true);
		assertEquals(PlayerState.MOVING, playerState(state).getBase());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void serializedSuperclassRerollResponseIsExecutedOnceOnResume() {
		GameState state = buildState();
		state.getGame().getTurnData().setReRolls(2);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 2, 3, 5);
		step.setReRolledAction(ReRolledActions.JUMP_UP);
		step.setReRollSource(ReRollSources.TEAM_RE_ROLL);
		StepJumpUp restored = new StepJumpUp(state).initFrom(state.getGame().getRules(), step.toJsonValue());
		restored.getResult().reset();

		restored.start();

		assertEquals(StepAction.NEXT_STEP, restored.getResult().getNextAction());
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		ReportJumpUpRoll report = (ReportJumpUpRoll) Arrays.stream(restored.getResult().getReportList().getReports())
			.filter(value -> value instanceof ReportJumpUpRoll).findFirst().get();
		assertEquals(3, report.getRoll());
		assertTrue(report.isReRolled());
		assertTrue(report.isSuccessful());
		assertFalse(state.getGame().getPlayerById("jumper").isUsed(professional(state)));

		restored.initFrom(state.getGame().getRules(), restored.toJsonValue());
		restored.getResult().reset();
		restored.start();
		assertEquals(StepAction.NEXT_STEP, restored.getResult().getNextAction());
		assertEquals(0, restored.getResult().getReportList().size());
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void serializedSuperclassDeclineResponseEndsActivationOnResume() {
		GameState state = buildState();
		state.getGame().getTurnData().setReRolls(1);
		StepJumpUp step = (StepJumpUp) jumpUp(state, 2);
		assertSerializedDecline(state, step);
	}

	@Test
	void serializedSuperclassDeclineAfterFailedRerollEndsActivationOnResume() {
		GameState state = buildState();
		state.getGame().getTurnData().setReRolls(2);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 1, 2, 5);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.TEAM_RE_ROLL));
		assertSerializedDecline(state, step);
	}

	private void assertSerializedDecline(GameState state, StepJumpUp step) {
		step.setReRolledAction(ReRolledActions.JUMP_UP);
		step.setReRollSource(null);
		StepJumpUp restored = new StepJumpUp(state).initFrom(state.getGame().getRules(), step.toJsonValue());
		restored.getResult().reset();

		restored.start();

		assertEquals(StepAction.GOTO_LABEL, restored.getResult().getNextAction());
		assertEquals(PlayerState.PRONE, playerState(state).getBase());
		assertFalse(playerState(state).isActive());
		assertTrue(state.getGame().isHomePlaying());
		assertNull(state.getGame().getDialogParameter());
		assertEquals(0, restored.getResult().getReportList().size());
		assertFalse(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	private void assertJumpUpReport(IStep step, int roll, boolean successful, boolean reRolled) {
		ReportJumpUpRoll[] reports = Arrays.stream(step.getGameState().getGameLog().getServerCommands())
			.filter(command -> command instanceof ServerCommandModelSync)
			.flatMap(command -> Arrays.stream(((ServerCommandModelSync) command).getReportList().getReports()))
			.filter(report -> report instanceof ReportJumpUpRoll).toArray(ReportJumpUpRoll[]::new);
		assertEquals(reRolled ? 2 : 1, reports.length);
		assertFalse(reports[0].isReRolled());
		ReportJumpUpRoll latest = reports[reports.length - 1];
		assertEquals(roll, latest.getRoll());
		assertEquals(successful, latest.isSuccessful());
		assertEquals(reRolled, latest.isReRolled());
	}

	@Test
	void failedTeamRerollEndsOnlyActivationEvenWithResourcesRemaining() {
		GameState state = buildState("BB2025", 4, "Pro");
		state.getGame().getTurnData().setReRolls(2);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 1, 1, 5);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.TEAM_RE_ROLL));
		assertJumpUpReport(step, 1, false, true);
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		assertFailedWithoutTurnover(state);
	}

	@Test
	void usedTeamAndProResourcesAreNotOffered() {
		GameState state = buildState("BB2025", 4, "Consummate Professional", "Pro");
		state.getGame().getTurnData().setReRolls(0);
		state.getGame().getFieldModel().setPlayerState(state.getGame().getPlayerById("jumper"),
			playerState(state).changeUsedPro(true));
		jumpUp(state, 2);
		assertFalse(dialog(state).hasProperty(ReRollProperty.TRR));
		assertFalse(dialog(state).hasProperty(ReRollProperty.PRO));
		chooseModifier(state);
		assertEquals(0, state.getGame().getTurnData().getReRolls());
		assertTrue(playerState(state).hasUsedPro());
	}

	@Test
	void proCanRerollJumpUpSuccessfullyWithoutSpendingTeamResource() {
		GameState state = buildState("BB2025", 4, "Pro");
		state.getGame().getTurnData().setReRolls(1);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 1, 3, 3, 5);
		assertTrue(dialog(state).hasProperty(ReRollProperty.PRO));
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.PRO));
		assertJumpUpReport(step, 3, true, true);
		assertEquals(PlayerState.MOVING, playerState(state).getBase());
		assertTrue(playerState(state).hasUsedPro());
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void successfulProFollowedByFailedJumpUpCannotRerollAgain() {
		GameState state = buildState("BB2025", 4, "Pro");
		state.getGame().getTurnData().setReRolls(1);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 1, 3, 1, 5);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.PRO));
		assertJumpUpReport(step, 1, false, true);
		assertTrue(playerState(state).hasUsedPro());
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		assertFailedWithoutTurnover(state);
	}

	@Test
	void failedProEndsActivationWithoutRollingJumpUpAgain() {
		GameState state = buildState("BB2025", 4, "Consummate Professional", "Pro");
		state.getGame().getTurnData().setReRolls(1);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 2, 1, 5);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.PRO));
		assertJumpUpReport(step, 2, false, false);
		assertTrue(playerState(state).hasUsedPro());
		assertFalse(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		assertFailedWithoutTurnover(state);
	}

	@Test
	void proTeamFallbackRerollsProThenJumpUpThroughStandardMechanic() {
		GameState state = buildState("BB2025", 4, "Pro");
		state.getGame().getTurnData().setReRolls(1);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 1, 1, 3, 3, 5);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.PRO_TRR));
		assertJumpUpReport(step, 3, true, true);
		assertTrue(playerState(state).hasUsedPro());
		assertEquals(0, state.getGame().getTurnData().getReRolls());
		assertEquals(PlayerState.MOVING, playerState(state).getBase());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void failedLonerSpendsTeamResourceButDoesNotRerollJumpUp() {
		GameState state = buildState("BB2025", 4, "Loner");
		state.getGame().getTurnData().setReRolls(1);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 1, 1, 5);
		assertTrue(dialog(state).hasProperty(ReRollProperty.LONER));
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.TEAM_RE_ROLL));
		assertJumpUpReport(step, 1, false, false);
		assertEquals(0, state.getGame().getTurnData().getReRolls());
		assertFailedWithoutTurnover(state);
	}

	@Test
	void successfulLonerAllowsTeamReroll() {
		GameState state = buildState("BB2025", 4, "Loner");
		state.getGame().getTurnData().setReRolls(1);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 1, 4, 3, 5);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.TEAM_RE_ROLL));
		assertJumpUpReport(step, 3, true, true);
		assertEquals(0, state.getGame().getTurnData().getReRolls());
		assertEquals(PlayerState.MOVING, playerState(state).getBase());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void proIsOfferedWithoutTeamRerollAndCanBeDeclined() {
		GameState state = buildState("BB2025", 4, "Pro");
		jumpUp(state, 1);
		assertTrue(dialog(state).hasProperty(ReRollProperty.PRO));
		assertFalse(dialog(state).hasProperty(ReRollProperty.TRR));
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, null));
		assertFalse(playerState(state).hasUsedPro());
		assertFailedWithoutTurnover(state);
	}

	@Test
	void usedSingleUseSkillAndBlockOnlyLordOfChaosDoNotOfferJumpUpReroll() {
		GameState state = buildState("BB2025", 4, "Halfling Luck", "Lord of Chaos");
		state.getGame().getPlayerById("jumper").markUsed(skill(state, "Halfling Luck"), state.getGame());
		jumpUp(state, 1);
		assertFailedWithoutTurnover(state);
	}

	@Test
	void singleUseSkillCanRerollAndIsConsumed() {
		GameState state = buildState("BB2025", 4, "Halfling Luck");
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 1, 3, 5);
		Skill luck = skill(state, "Halfling Luck");
		assertEquals(luck, dialog(state).getReRollSkill());
		StepEngine.respond(state, new ClientCommandUseSkill(luck, true, "jumper", ReRolledActions.JUMP_UP, false));
		assertJumpUpReport(step, 3, true, true);
		assertTrue(state.getGame().getPlayerById("jumper").isUsed(luck));
		assertEquals(PlayerState.MOVING, playerState(state).getBase());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void successfulProCanBeFollowedByModifierRescuingFailedReroll() {
		GameState state = buildState("BB2025", 4, "Consummate Professional", "Pro");
		state.getGame().getTurnData().setReRolls(1);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 1, 3, 2, 5);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.PRO));
		assertJumpUpReport(step, 2, false, true);
		assertFalse(dialog(state).hasProperty(ReRollProperty.TRR));
		assertFalse(dialog(state).hasProperty(ReRollProperty.PRO));
		chooseModifier(state);
		assertEquals(PlayerState.MOVING, playerState(state).getBase());
		assertTrue(playerState(state).hasUsedPro());
		assertTrue(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		assertJumpUpReport(step, 2, false, true);
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void decliningModifierAfterFailedRerollDoesNotSpendAnotherResource() {
		GameState state = buildState("BB2025", 4, "Consummate Professional", "Pro");
		state.getGame().getTurnData().setReRolls(2);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 1, 2, 5);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.TEAM_RE_ROLL));
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, null));
		assertJumpUpReport(step, 2, false, true);
		assertFalse(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertFalse(playerState(state).hasUsedPro());
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		assertFailedWithoutTurnover(state);
	}

	@Test
	void failedRerollCanBeRescuedAfterResumeWithoutSpendingAgain() {
		GameState state = buildState("BB2025", 4, "Consummate Professional", "Pro");
		state.getGame().getTurnData().setReRolls(2);
		StepJumpUp step = (StepJumpUp) jumpUpWithRolls(state, 1, 2, 5);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.TEAM_RE_ROLL));
		assertJumpUpReport(step, 2, false, true);
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		assertFalse(dialog(state).hasProperty(ReRollProperty.TRR));
		assertFalse(dialog(state).hasProperty(ReRollProperty.PRO));
		assertNull(dialog(state).getReRollSkill());
		assertEquals(1, dialog(state).getModifierOptions().size());

		JsonObject saved = step.toJsonValue();
		step.initFrom(state.getGame().getRules(), saved);
		assertEquals(saved, step.toJsonValue());
		step.getResult().reset();
		step.start();
		assertEquals(2, dialog(state).getRoll());
		assertFalse(step.getResult().getReportList().hasReport(ReportId.JUMP_UP_ROLL));
		assertEquals(StepCommandStatus.EXECUTE_STEP, step.handleCommand(new ReceivedCommand(
			new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.TEAM_RE_ROLL), null)));
		chooseModifier(state);
		assertEquals(PlayerState.MOVING, playerState(state).getBase());
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		assertTrue(state.getGame().getPlayerById("jumper").isUsed(professional(state)));
		assertFalse(playerState(state).hasUsedPro());
		assertFalse(step.getResult().getReportList().hasReport(ReportId.JUMP_UP_ROLL));
		StepJumpUp restored = new StepJumpUp(state).initFrom(state.getGame().getRules(), step.toJsonValue());
		restored.getResult().reset();
		restored.start();
		assertEquals(StepAction.NEXT_STEP, restored.getResult().getNextAction());
		assertEquals(0, restored.getResult().getReportList().size());
		assertEquals(1, state.getGame().getTurnData().getReRolls());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void savedDeclineDoesNotReopenModifierChoiceOrRollAgain() {
		GameState state = buildState();
		StepJumpUp step = (StepJumpUp) jumpUp(state, 2);
		step.getResult().reset();
		assertEquals(StepCommandStatus.EXECUTE_STEP, step.handleCommand(new ReceivedCommand(
			new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, null), null)));
		JsonObject saved = step.toJsonValue();
		StepJumpUp restored = new StepJumpUp(state).initFrom(state.getGame().getRules(), saved);
		assertEquals(saved, restored.toJsonValue());
		restored.start();
		assertEquals(StepAction.GOTO_LABEL, restored.getResult().getNextAction());
		assertNull(state.getGame().getDialogParameter());
		assertFalse(restored.getResult().getReportList().hasReport(ReportId.JUMP_UP_ROLL));
		assertEquals(PlayerState.PRONE, playerState(state).getBase());
		assertFalse(playerState(state).isActive());
		assertTrue(state.getGame().isHomePlaying());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void factoriesAndSerializedStepIdentityForBb2016() {
		assertFactoryMapping("BB2016");
	}

	@Test
	void factoriesAndSerializedStepIdentityForBb2020() {
		assertFactoryMapping("BB2020");
	}

	@Test
	void factoriesAndSerializedStepIdentityForBb2025() {
		assertFactoryMapping("BB2025");
	}

	private void assertFactoryMapping(String rules) {
		GameState state = buildState(rules, 4, "Consummate Professional");
		Game game = state.getGame();
		IStep step = state.getStepFactory().forStepId(StepId.JUMP_UP);
		assertEquals(rules.equals("BB2025") ? StepJumpUp.class
			: com.fumbbl.ffb.server.step.mixed.action.select.StepJumpUp.class, step.getClass());
		assertEquals("jumpUp", StepId.JUMP_UP.getName());
		assertEquals("com.fumbbl.ffb.server.skillbehaviour." + rules.toLowerCase() + ".JumpUpBehaviour",
			skill(state, "Jump Up").getSkillBehaviour().getClass().getName());
		assertEquals(step.getClass(), state.getStepFactory().forJsonValue(game.getRules(), step.toJsonValue()).getClass());
		JumpUpModifierFactory factory = game.getFactory(Factory.JUMP_UP_MODIFIER);
		assertNotNull(factory.forName("Jump Up"));
		if (rules.equals("BB2025")) {
			assertNotNull(factory.forName("Consummate Professional"));
		} else {
			assertNull(factory.forName("Consummate Professional"));
		}
		game.getActingPlayer().setPlayerId("jumper");
		assertEquals(1, factory.findModifiers(new JumpUpContext(game.getActingPlayer(), game)).size());
		if (rules.equals("BB2025")) {
			assertEquals(2, factory.findModifiers(new JumpUpContext(game.getActingPlayer(), game,
				Collections.singleton(professional(state)))).size());
		}
	}

	@Test
	void bb2016KeepsTeamRerollInsteadOfModifierSelection() {
		assertLegacyTeamReroll("BB2016");
	}

	@Test
	void bb2020KeepsTeamRerollInsteadOfModifierSelection() {
		assertLegacyTeamReroll("BB2020");
	}

	private void assertLegacyTeamReroll(String rules) {
		GameState state = buildState(rules, 4);
		state.getGame().getTurnData().setReRolls(1);
		assertEquals(StepId.JUMP_UP, jumpUp(state, 1).getId());
		assertNotNull(state.getGame().getDialogParameter());
		assertFalse(state.getGame().getDialogParameter() instanceof DialogReRollModifierChoiceParameter);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.TEAM_RE_ROLL));
		assertEquals(PlayerState.MOVING, playerState(state).getBase());
		assertEquals(0, state.getGame().getTurnData().getReRolls());
		assertTrue(state.getGame().isHomePlaying());
	}
}
