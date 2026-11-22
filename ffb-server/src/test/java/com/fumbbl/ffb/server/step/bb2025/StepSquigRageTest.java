package com.fumbbl.ffb.server.step.bb2025;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportConfusionRoll;
import com.fumbbl.ffb.report.bb2025.ReportSquigRage;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.model.SteadyFootingContext;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.StepStack;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.bb2025.SquigRage;
import com.fumbbl.ffb.util.UtilPlayer;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StepSquigRageTest {
	private static final String FAILURE_LABEL = "endSelecting";
	private static final PlayerState STANDING = new PlayerState(PlayerState.STANDING).changeActive(true);

	private final SquigRage squigRage = new SquigRage();
	private final List<StepParameter> publishedParameters = new ArrayList<>();

	private GameState gameState;
	private Game game;
	private FieldModel fieldModel;
	private DiceRoller diceRoller;
	private ActingPlayer actingPlayer;
	private Team team;
	private Team otherTeam;
	private Player<?> squig;
	private Player<?> opponent;
	private Player<?> teamMate;
	private FieldCoordinate squigCoordinate;

	private MockedStatic<UtilPlayer> utilPlayer;
	private MockedStatic<UtilServerDialog> utilServerDialog;
	private MockedStatic<UtilServerReRoll> utilServerReRoll;
	private MockedStatic<UtilServerSteps> utilServerSteps;
	private MockedStatic<UtilServerInjury> utilServerInjury;

	private StepSquigRage step;

	@BeforeEach
	void setUp() {
		squigRage.postConstruct();
		gameState = mock(GameState.class);
		game = mock(Game.class);
		fieldModel = mock(FieldModel.class);
		diceRoller = mock(DiceRoller.class);
		team = mock(Team.class);
		otherTeam = mock(Team.class);
		squig = mock(Player.class);
		opponent = mock(Player.class);
		teamMate = mock(Player.class);
		FantasyFootballServer server = mock(FantasyFootballServer.class);
		StepStack stepStack = mock(StepStack.class);
		GameMechanic gameMechanic = mock(GameMechanic.class);

		when(gameState.getGame()).thenReturn(game);
		when(gameState.getDiceRoller()).thenReturn(diceRoller);
		when(gameState.getServer()).thenReturn(server);
		when(server.getDebugLog()).thenReturn(mock(DebugLog.class));
		when(gameState.getStepStack()).thenReturn(stepStack);
		doAnswer(invocation -> publishedParameters.add(invocation.getArgument(0)))
			.when(stepStack).publishStepParameter(any());

		when(game.getFieldModel()).thenReturn(fieldModel);
		when(game.getTurnMode()).thenReturn(TurnMode.REGULAR);
		when(game.getActingTeam()).thenReturn(team);
		when(game.getOtherTeam(team)).thenReturn(otherTeam);
		doReturn(gameMechanic).when(game).getMechanic(Mechanic.Type.GAME);
		when(gameMechanic.isBlockActionAllowed(TurnMode.REGULAR)).thenReturn(true);
		when(team.getId()).thenReturn("team");

		when(squig.getId()).thenReturn("squig");
		when(squig.getTeam()).thenReturn(team);
		when(squig.getSkillWithProperty(NamedProperties.requiresRageRollBeforeDeclaringAction)).thenReturn(squigRage);
		when(opponent.getId()).thenReturn("opponent");
		when(teamMate.getId()).thenReturn("teamMate");
		when(teamMate.getTeam()).thenReturn(team);
		when(game.getPlayerById("squig")).thenAnswer(invocation -> squig);
		when(game.getPlayerById("teamMate")).thenAnswer(invocation -> teamMate);
		when(game.getPlayerById("opponent")).thenAnswer(invocation -> opponent);

		squigCoordinate = new FieldCoordinate(10, 7);
		when(fieldModel.getPlayerCoordinate(squig)).thenReturn(squigCoordinate);
		when(fieldModel.getPlayerCoordinate(teamMate)).thenReturn(new FieldCoordinate(9, 7));
		when(fieldModel.getPlayerState(squig)).thenReturn(STANDING);

		actingPlayer = new ActingPlayer(game);
		actingPlayer.setPlayerId("squig");
		actingPlayer.setPlayerAction(PlayerAction.SQUIG_RAGE);
		when(game.getActingPlayer()).thenReturn(actingPlayer);

		utilPlayer = mockStatic(UtilPlayer.class);
		utilServerDialog = mockStatic(UtilServerDialog.class);
		utilServerReRoll = mockStatic(UtilServerReRoll.class);
		utilServerSteps = mockStatic(UtilServerSteps.class);
		utilServerInjury = mockStatic(UtilServerInjury.class);
		givenOpponents(opponent);
		givenTeamMates(teamMate);
		utilServerSteps.when(() -> UtilServerSteps.checkCommandIsFromCurrentPlayer(any(), any())).thenReturn(true);

		step = new StepSquigRage(gameState);
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(new StepParameter(StepParameterKey.GOTO_LABEL_ON_FAILURE, FAILURE_LABEL));
		step.init(parameters);
	}

	@AfterEach
	void tearDown() {
		utilPlayer.close();
		utilServerDialog.close();
		utilServerReRoll.close();
		utilServerSteps.close();
		utilServerInjury.close();
	}

	@Test
	void requiresFailureLabel() {
		StepSquigRage uninitialized = new StepSquigRage(gameState);

		assertThrows(StepException.class, () -> uninitialized.init(new StepParameterSet()));
	}

	@Test
	void successfulRollLetsPlayerDeclareAnAction() {
		when(diceRoller.rollSkill()).thenReturn(3);

		step.start();

		assertEquals(StepAction.NEXT_STEP, step.getResult().getNextAction());
		assertTrue(actingPlayer.isSkillUsed(squigRage));
		ReportConfusionRoll roll = report(ReportConfusionRoll.class).orElseThrow(AssertionError::new);
		assertTrue(roll.isSuccessful());
		assertFalse(report(ReportSquigRage.class).isPresent());
		utilServerSteps.verify(() -> UtilServerSteps.changePlayerAction(any(), any(), any(), anyBoolean()), never());
		assertFalse(actingPlayer.isMustCompleteAction());
	}

	@Test
	void failedRollOffersReRoll() {
		when(diceRoller.rollSkill()).thenReturn(2);
		utilServerReRoll.when(() -> UtilServerReRoll.askForReRollIfAvailable(any(GameState.class), any(Player.class),
			any(ReRolledAction.class), anyInt(), anyBoolean())).thenReturn(true);

		step.start();

		assertEquals(StepAction.CONTINUE, step.getResult().getNextAction());
		utilServerReRoll.verify(() -> UtilServerReRoll.askForReRollIfAvailable(eq(gameState), eq(squig),
			eq(ReRolledActions.SQUIG_RAGE), eq(3), eq(false)));
		assertFalse(report(ReportSquigRage.class).isPresent());
	}

	@Test
	void failedRollNextToOpponentForcesBlock() {
		when(diceRoller.rollSkill()).thenReturn(1);

		step.start();

		assertEquals(StepAction.NEXT_STEP, step.getResult().getNextAction());
		assertEquals(ReportSquigRage.Outcome.MUST_BLOCK, squigRageReport().getOutcome());
		utilServerSteps.verify(() -> UtilServerSteps.changePlayerAction(step, "squig", PlayerAction.BLOCK, false));
		assertTrue(actingPlayer.isMustCompleteAction());
	}

	@Test
	void declinedReRollRagesWithoutRollingAgain() {
		actingPlayer.markSkillUsed(squigRage);
		step.setReRolledAction(ReRolledActions.SQUIG_RAGE);

		step.start();

		verify(diceRoller, never()).rollSkill();
		assertEquals(ReportSquigRage.Outcome.MUST_BLOCK, squigRageReport().getOutcome());
	}

	@Test
	void failedReRollRages() {
		actingPlayer.markSkillUsed(squigRage);
		step.setReRolledAction(ReRolledActions.SQUIG_RAGE);
		step.setReRollSource(ReRollSources.TEAM_RE_ROLL);
		utilServerReRoll.when(() -> UtilServerReRoll.useReRoll(any(), any(), any())).thenReturn(true);
		when(diceRoller.rollSkill()).thenReturn(2);

		step.start();

		verify(diceRoller, times(1)).rollSkill();
		assertTrue(report(ReportConfusionRoll.class).orElseThrow(AssertionError::new).isReRolled());
		utilServerReRoll.verify(() -> UtilServerReRoll.askForReRollIfAvailable(any(GameState.class), any(Player.class),
			any(ReRolledAction.class), anyInt(), anyBoolean()), never());
		assertEquals(ReportSquigRage.Outcome.MUST_BLOCK, squigRageReport().getOutcome());
	}

	@Test
	void alreadyRolledPlayerIsNotRolledForAgain() {
		actingPlayer.markSkillUsed(squigRage);

		step.start();

		verify(diceRoller, never()).rollSkill();
		assertEquals(StepAction.NEXT_STEP, step.getResult().getNextAction());
	}

	@Test
	void otherActionsAreIgnored() {
		actingPlayer.setPlayerAction(PlayerAction.MOVE);

		step.start();

		verify(diceRoller, never()).rollSkill();
		assertEquals(StepAction.NEXT_STEP, step.getResult().getNextAction());
	}

	@Test
	void failedRollWithoutOpponentOffersAdjacentTeamMates() {
		givenOpponents();
		givenTeamMates(teamMate);
		when(diceRoller.rollSkill()).thenReturn(1);

		step.start();

		assertEquals(StepAction.CONTINUE, step.getResult().getNextAction());
		ArgumentCaptor<IDialogParameter> dialog = ArgumentCaptor.forClass(IDialogParameter.class);
		utilServerDialog.verify(() -> UtilServerDialog.showDialog(eq(gameState), dialog.capture(), eq(false)));
		DialogPlayerChoiceParameter choice = (DialogPlayerChoiceParameter) dialog.getValue();
		assertEquals(PlayerChoiceMode.SQUIG_RAGE, choice.getPlayerChoiceMode());
		assertArrayEquals(new String[]{"teamMate"}, choice.getPlayerIds());
		assertEquals(0, choice.getMinSelects());
		assertEquals(1, choice.getMaxSelects());
	}

	@Test
	void choosingNoTeamMateMakesPlayerDistracted() {
		givenTeamMateChoiceOffered();

		StepCommandStatus status = step.handleCommand(choice());

		assertEquals(StepCommandStatus.SKIP_STEP, status);
		assertDistracted();
		utilServerDialog.verify(() -> UtilServerDialog.hideDialog(gameState));
	}

	@Test
	void choosingTeamMateNotOfferedIsIgnored() {
		givenTeamMateChoiceOffered();

		StepCommandStatus status = step.handleCommand(choice(opponent));

		assertNotEquals(StepCommandStatus.SKIP_STEP, status);
		utilServerDialog.verify(() -> UtilServerDialog.hideDialog(gameState), never());
		utilServerInjury.verify(() -> UtilServerInjury.handleInjury(any(), any(), any(), any(), any(), any(), any(), any()),
			never());
	}

	@Test
	void choiceIsIgnoredWhileNoTeamMatesAreOffered() {
		StepCommandStatus status = step.handleCommand(choice(teamMate));

		assertNotEquals(StepCommandStatus.SKIP_STEP, status);
		utilServerInjury.verify(() -> UtilServerInjury.handleInjury(any(), any(), any(), any(), any(), any(), any(), any()),
			never());
	}

	@Test
	void choosingTeamMateKnocksThemDown() {
		givenTeamMateChoiceOffered();
		InjuryResult injuryResult = mock(InjuryResult.class);
		utilServerInjury.when(() -> UtilServerInjury.handleInjury(any(), any(), any(), any(), any(), any(), any(), any()))
			.thenReturn(injuryResult);

		StepCommandStatus status = step.handleCommand(choice(teamMate));

		assertEquals(StepCommandStatus.SKIP_STEP, status);
		assertEquals(StepAction.NEXT_STEP, step.getResult().getNextAction());
		ReportSquigRage report = squigRageReport();
		assertEquals(ReportSquigRage.Outcome.KNOCKS_DOWN_TEAM_MATE, report.getOutcome());
		assertEquals("teamMate", report.getTeamMateId());
		utilServerInjury.verify(() -> UtilServerInjury.handleInjury(eq(step), any(), eq(squig), eq(teamMate),
			eq(new FieldCoordinate(9, 7)), isNull(), isNull(), eq(ApothecaryMode.SQUIG_RAGE)));
		SteadyFootingContext context = (SteadyFootingContext) published(StepParameterKey.STEADY_FOOTING_CONTEXT)
			.orElseThrow(AssertionError::new).getValue();
		assertEquals(injuryResult, context.getDropPlayerContext().getInjuryResult());
		assertEquals(ApothecaryMode.SQUIG_RAGE, context.getDropPlayerContext().getApothecaryMode());
		assertFalse(context.getDropPlayerContext().isEndTurn());
	}

	@Test
	void knockingDownBallCarryingTeamMateEndsTheTurn() {
		givenTeamMateChoiceOffered();
		utilPlayer.when(() -> UtilPlayer.hasBall(game, teamMate)).thenReturn(true);

		step.handleCommand(choice(teamMate));

		SteadyFootingContext context = (SteadyFootingContext) published(StepParameterKey.STEADY_FOOTING_CONTEXT)
			.orElseThrow(AssertionError::new).getValue();
		assertTrue(context.getDropPlayerContext().isEndTurn());
	}

	@Test
	void failedRollWithoutAnyTargetMakesPlayerDistracted() {
		givenOpponents();
		givenTeamMates();
		when(diceRoller.rollSkill()).thenReturn(1);

		step.start();

		assertDistracted();
		utilServerDialog.verify(() -> UtilServerDialog.showDialog(any(), any(), anyBoolean()), never());
	}

	private void givenTeamMateChoiceOffered() {
		givenOpponents();
		givenTeamMates(teamMate);
		when(diceRoller.rollSkill()).thenReturn(1);
		step.start();
	}

	private void givenOpponents(Player<?>... opponents) {
		utilPlayer.when(() -> UtilPlayer.findAdjacentBlockablePlayers(game, otherTeam, squigCoordinate))
			.thenReturn(opponents);
	}

	private void givenTeamMates(Player<?>... teamMates) {
		utilPlayer.when(() -> UtilPlayer.findAdjacentBlockablePlayers(game, team, squigCoordinate))
			.thenReturn(teamMates);
	}

	private void assertDistracted() {
		assertEquals(ReportSquigRage.Outcome.DISTRACTED, squigRageReport().getOutcome());
		ArgumentCaptor<PlayerState> state = ArgumentCaptor.forClass(PlayerState.class);
		verify(fieldModel).setPlayerState(eq(squig), state.capture());
		assertTrue(state.getValue().isConfused());
		assertFalse(state.getValue().isActive());
		assertEquals(PlayerState.STANDING, state.getValue().getBase());
		assertEquals(SoundId.DUH, step.getResult().getSound());
		assertEquals(StepAction.GOTO_LABEL, step.getResult().getNextAction());
		assertEquals(FAILURE_LABEL, step.getResult().getNextActionParameter());
		assertEquals(Boolean.TRUE, published(StepParameterKey.END_PLAYER_ACTION).orElseThrow(AssertionError::new).getValue());
	}

	private ReceivedCommand choice(Player<?>... players) {
		return new ReceivedCommand(new ClientCommandPlayerChoice(PlayerChoiceMode.SQUIG_RAGE, players), mock(Session.class));
	}

	private ReportSquigRage squigRageReport() {
		return report(ReportSquigRage.class).orElseThrow(AssertionError::new);
	}

	private <T extends IReport> Optional<T> report(Class<T> type) {
		return Arrays.stream(step.getResult().getReportList().getReports()).filter(type::isInstance).map(type::cast)
			.reduce((first, second) -> second);
	}

	private Optional<StepParameter> published(StepParameterKey key) {
		return publishedParameters.stream().filter(parameter -> parameter.getKey() == key).reduce((first, second) -> second);
	}
}
