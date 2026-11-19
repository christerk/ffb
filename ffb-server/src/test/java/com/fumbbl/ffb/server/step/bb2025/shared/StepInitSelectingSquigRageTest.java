package com.fumbbl.ffb.server.step.bb2025.shared;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandConfirm;
import com.fumbbl.ffb.net.commands.ClientCommandEndTurn;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.StepStack;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.skill.bb2025.SquigRage;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StepInitSelectingSquigRageTest {
	private static final String END_LABEL = "endSelecting";
	private static final PlayerState MOVING = new PlayerState(PlayerState.MOVING).changeActive(true);

	private final SquigRage squigRage = new SquigRage();
	private final List<StepParameter> publishedParameters = new ArrayList<>();

	private GameState gameState;
	private Game game;
	private FieldModel fieldModel;
	private TurnData turnData;
	private ActingPlayer actingPlayer;
	private Player<?> squig;
	private MockedStatic<UtilServerSteps> utilServerSteps;
	private StepInitSelecting step;

	@BeforeEach
	void setUp() {
		squigRage.postConstruct();
		gameState = mock(GameState.class);
		game = mock(Game.class);
		fieldModel = mock(FieldModel.class);
		turnData = mock(TurnData.class);
		squig = mock(Player.class);
		FantasyFootballServer server = mock(FantasyFootballServer.class);
		StepStack stepStack = mock(StepStack.class);

		when(gameState.getGame()).thenReturn(game);
		when(gameState.getServer()).thenReturn(server);
		when(server.getDebugLog()).thenReturn(mock(DebugLog.class));
		when(gameState.getStepStack()).thenReturn(stepStack);
		doAnswer(invocation -> publishedParameters.add(invocation.getArgument(0)))
			.when(stepStack).publishStepParameter(any());

		when(game.getFieldModel()).thenReturn(fieldModel);
		when(game.getTurnMode()).thenReturn(TurnMode.REGULAR);
		when(game.getTurnData()).thenReturn(turnData);
		when(squig.getId()).thenReturn("squig");
		when(squig.getSkillWithProperty(NamedProperties.requiresRageRollBeforeDeclaringAction)).thenReturn(squigRage);
		when(game.getPlayerById("squig")).thenAnswer(invocation -> squig);

		actingPlayer = new ActingPlayer(game);
		actingPlayer.setPlayerId("squig");
		when(game.getActingPlayer()).thenReturn(actingPlayer);

		utilServerSteps = mockStatic(UtilServerSteps.class);
		utilServerSteps.when(() -> UtilServerSteps.checkCommandIsFromCurrentPlayer(any(), any())).thenReturn(true);

		step = new StepInitSelecting(gameState);
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(new StepParameter(StepParameterKey.GOTO_LABEL_ON_END, END_LABEL));
		step.init(parameters);
	}

	@AfterEach
	void tearDown() {
		utilServerSteps.close();
	}

	@Test
	void endTurnIsIgnoredWhileForcedBlockIsPending() {
		givenForcedBlock();

		StepCommandStatus status = step.handleCommand(command(new ClientCommandEndTurn(TurnMode.REGULAR, null)));

		assertNotEquals(StepCommandStatus.EXECUTE_STEP, status);
		assertFalse(isPublished(StepParameterKey.END_TURN));
	}

	@Test
	void endTurnIsAcceptedOnceForcedBlockIsPerformed() {
		givenForcedBlock();
		actingPlayer.setHasBlocked(true);

		StepCommandStatus status = step.handleCommand(command(new ClientCommandEndTurn(TurnMode.REGULAR, null)));

		assertEquals(StepCommandStatus.EXECUTE_STEP, status);
		assertTrue(isPublished(StepParameterKey.END_TURN));
		assertEquals(StepAction.GOTO_LABEL, step.getResult().getNextAction());
	}

	@Test
	void confirmingEndOfActionIsIgnoredWhileForcedBlockIsPending() {
		givenForcedBlock();

		StepCommandStatus status = step.handleCommand(command(new ClientCommandConfirm()));

		assertNotEquals(StepCommandStatus.EXECUTE_STEP, status);
		assertFalse(isPublished(StepParameterKey.END_PLAYER_ACTION));
	}

	@Test
	void confirmingEndOfActionIsAcceptedWithoutForcedBlock() {
		actingPlayer.setPlayerAction(PlayerAction.BLOCK);

		StepCommandStatus status = step.handleCommand(command(new ClientCommandConfirm()));

		assertEquals(StepCommandStatus.EXECUTE_STEP, status);
		assertTrue(isPublished(StepParameterKey.END_PLAYER_ACTION));
	}

	@Test
	void deselectingIsIgnoredWhileForcedBlockIsPending() {
		givenForcedBlock();

		StepCommandStatus status = step.handleCommand(command(new ClientCommandActingPlayer(null, null, false)));

		assertEquals(StepCommandStatus.SKIP_STEP, status);
		assertFalse(isPublished(StepParameterKey.END_PLAYER_ACTION));
	}

	@Test
	void cancellingUncommittedBlitzAfterRollDeactivatesPlayer() {
		actingPlayer.markSkillUsed(squigRage);
		givenUncommittedBlitz();

		StepCommandStatus status = step.handleCommand(command(new ClientCommandActingPlayer(null, null, false)));

		assertEquals(StepCommandStatus.EXECUTE_STEP, status);
		assertTrue(isPublished(StepParameterKey.END_PLAYER_ACTION));
		verify(turnData).setBlitzUsed(false);
		assertFalse(restoredState().isActive());
	}

	@Test
	void cancellingUncommittedBlitzWithoutRollKeepsPlayerActive() {
		givenUncommittedBlitz();

		step.handleCommand(command(new ClientCommandActingPlayer(null, null, false)));

		verify(turnData).setBlitzUsed(false);
		assertTrue(restoredState().isActive());
	}

	private void givenForcedBlock() {
		actingPlayer.markSkillUsed(squigRage);
		actingPlayer.setPlayerAction(PlayerAction.BLOCK);
		actingPlayer.setMustCompleteAction(true);
	}

	private void givenUncommittedBlitz() {
		actingPlayer.setPlayerAction(PlayerAction.BLITZ_MOVE);
		TargetSelectionState targetSelectionState = new TargetSelectionState("opponent");
		targetSelectionState.setOldActingPlayerState(MOVING);
		when(fieldModel.getTargetSelectionState()).thenReturn(targetSelectionState);
	}

	private PlayerState restoredState() {
		ArgumentCaptor<PlayerState> state = ArgumentCaptor.forClass(PlayerState.class);
		verify(fieldModel).setPlayerState(eq(squig), state.capture());
		return state.getValue();
	}

	private boolean isPublished(StepParameterKey key) {
		return publishedParameters.stream().anyMatch(parameter -> parameter.getKey() == key);
	}

	private ReceivedCommand command(NetCommand command) {
		return new ReceivedCommand(command, mock(Session.class));
	}
}
