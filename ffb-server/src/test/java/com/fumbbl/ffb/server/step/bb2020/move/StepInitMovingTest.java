package com.fumbbl.ffb.server.step.bb2020.move;

import com.fumbbl.ffb.CommonPropertyValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandMove;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StepInitMovingTest {

	private static final String ACTING_PLAYER = "actingPlayer";

	private static final FieldCoordinate START = new FieldCoordinate(1, 1);

	private static final FieldCoordinate[] PATH = new FieldCoordinate[]{new FieldCoordinate(2, 1), new FieldCoordinate(2, 2)};

	private static final FieldCoordinate[] END = new FieldCoordinate[]{new FieldCoordinate(1, 2)};

	private GameState gameState;
	@Mock
	private Game game;

	@Mock()
	private Session session;

	@Captor
	private ArgumentCaptor<StepParameter> parameterCaptor;

	@BeforeEach
	public void setUp() {
		gameState = mock(GameState.class, Mockito.RETURNS_DEEP_STUBS);
		given(gameState.getGame()).willReturn(game);
	}

	@Test
	public void receiveMoveCommandHome() {

		try (MockedStatic<UtilServerSteps> serverSteps = mockStatic(UtilServerSteps.class);
				 MockedStatic<UtilServerPlayerMove> serverPlayerMove = mockStatic(UtilServerPlayerMove.class)) {

			StepInitMoving step = new StepInitMoving(gameState);

			ClientCommandMove move = new ClientCommandMove(ACTING_PLAYER, START, END, CommonPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS);
			ReceivedCommand wrapper = new ReceivedCommand(move, session);

			serverSteps.when(() -> UtilServerSteps.checkCommandIsFromCurrentPlayer(gameState, wrapper)).thenReturn(true);
			serverSteps.when(() -> UtilServerSteps.checkCommandIsFromHomePlayer(gameState, wrapper)).thenReturn(true);
			serverSteps.when(() -> UtilServerSteps.checkCommandWithActingPlayer(gameState, move)).thenReturn(true);
			serverPlayerMove.when(() -> UtilServerPlayerMove.isValidMove(gameState, move, true)).thenReturn(true);
			serverPlayerMove.when(() -> UtilServerPlayerMove.fetchFromSquare(move, true)).thenReturn(START);
			serverPlayerMove.when(() -> UtilServerPlayerMove.fetchMoveStack(move, true)).thenReturn(new FieldCoordinate[0]);

			StepCommandStatus result = step.handleCommand(wrapper);

			assertEquals(StepCommandStatus.EXECUTE_STEP, result);

			verify(gameState.getStepStack(), times(3)).publishStepParameter(parameterCaptor.capture());

			serverSteps.verify(() -> UtilServerSteps.checkCommandIsFromCurrentPlayer(gameState, wrapper));
			serverSteps.verify(() -> UtilServerSteps.checkCommandIsFromHomePlayer(gameState, wrapper));
			serverSteps.verify(() -> UtilServerSteps.checkCommandWithActingPlayer(gameState, move));
			serverSteps.verifyNoMoreInteractions();

			serverPlayerMove.verify(() -> UtilServerPlayerMove.isValidMove(gameState, move, true));
			serverPlayerMove.verify(() -> UtilServerPlayerMove.fetchFromSquare(move, true));
			serverPlayerMove.verify(() -> UtilServerPlayerMove.fetchMoveStack(move, true));
			serverPlayerMove.verifyNoMoreInteractions();

			parameterCaptor.getAllValues().forEach(param -> System.out.println(param.getKey() + ": " + param.getValue()));

			Optional<StepParameter> startParam = extract(StepParameterKey.MOVE_START);

			assertTrue(startParam.isPresent());
			assertEquals(START, startParam.get().getValue());

			Optional<StepParameter> stackParam = extract(StepParameterKey.MOVE_STACK);

			assertTrue(stackParam.isPresent());
			assertEquals(0, ((FieldCoordinate[])stackParam.get().getValue()).length);

		}
	}

	private Optional<StepParameter> extract(StepParameterKey key) {
		return parameterCaptor.getAllValues().stream().filter(param -> param.getKey() == key).findFirst();
	}

}