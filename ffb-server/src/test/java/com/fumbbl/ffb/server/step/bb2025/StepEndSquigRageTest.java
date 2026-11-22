package com.fumbbl.ffb.server.step.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StepEndSquigRageTest {
	private static final String FAILURE_LABEL = "endSelecting";

	private GameState gameState;
	private ActingPlayer actingPlayer;
	private StepEndSquigRage step;

	@BeforeEach
	void setUp() {
		gameState = mock(GameState.class);
		Game game = mock(Game.class);
		actingPlayer = mock(ActingPlayer.class);
		when(gameState.getGame()).thenReturn(game);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.SQUIG_RAGE);
		step = new StepEndSquigRage(gameState);
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(new StepParameter(StepParameterKey.GOTO_LABEL_ON_FAILURE, FAILURE_LABEL));
		step.init(parameters);
	}

	@Test
	void requiresFailureLabel() {
		StepEndSquigRage uninitialized = new StepEndSquigRage(gameState);

		assertThrows(StepException.class, () -> uninitialized.init(new StepParameterSet()));
	}

	@Test
	void continuesWithSelectSequence() {
		step.start();

		assertEquals(StepAction.NEXT_STEP, step.getResult().getNextAction());
	}

	@Test
	void resolvedRollAwaitsActionDeclaration() {
		step.start();

		verify(actingPlayer).setAwaitingActionDeclaration(true);
	}

	@Test
	void forcedBlockDoesNotAwaitActionDeclaration() {
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.BLOCK);

		step.start();

		verify(actingPlayer, never()).setAwaitingActionDeclaration(anyBoolean());
	}

	@Test
	void turnoverDoesNotAwaitActionDeclaration() {
		step.setParameter(new StepParameter(StepParameterKey.END_TURN, true));

		step.start();

		verify(actingPlayer, never()).setAwaitingActionDeclaration(anyBoolean());
	}

	@Test
	void turnoverJumpsToFailureLabel() {
		StepParameter endTurn = new StepParameter(StepParameterKey.END_TURN, true);

		assertTrue(step.setParameter(endTurn));
		step.start();

		assertEquals(StepAction.GOTO_LABEL, step.getResult().getNextAction());
		assertEquals(FAILURE_LABEL, step.getResult().getNextActionParameter());
		// the end turn parameter is still needed by the end selecting step
		assertFalse(endTurn.isConsumed());
	}

	@Test
	void otherParametersAreIgnored() {
		assertFalse(step.setParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true)));
		step.start();

		assertEquals(StepAction.NEXT_STEP, step.getResult().getNextAction());
	}
}
