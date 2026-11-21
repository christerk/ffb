package com.fumbbl.ffb.server.step.bb2025.start;

import com.fumbbl.ffb.dialog.DialogBuyPrayersAndInducementsParameter;
import com.fumbbl.ffb.dialog.DialogStartGameParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.StepAction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StepCheckpointTest {

	private GameState gameState;
	private Game game;
	private GameCache gameCache;

	@BeforeEach
	void setUp() {
		gameState = mock(GameState.class);
		game = mock(Game.class);
		gameCache = mock(GameCache.class);
		FantasyFootballServer server = mock(FantasyFootballServer.class);
		when(gameState.getGame()).thenReturn(game);
		when(gameState.getServer()).thenReturn(server);
		when(server.getGameCache()).thenReturn(gameCache);
	}

	@Test
	void persistsGameStateAfterMarkingStepAsFinished() {
		StepCheckpoint step = new StepCheckpoint(gameState);
		StepAction[] actionWhenPersisted = new StepAction[1];
		doAnswer(invocation -> {
			actionWhenPersisted[0] = step.getResult().getNextAction();
			return null;
		}).when(gameCache).queueDbUpdate(gameState, true);

		step.start();

		verify(gameCache).queueDbUpdate(gameState, true);
		assertEquals(StepAction.NEXT_STEP, actionWhenPersisted[0]);
		assertEquals(StepAction.NEXT_STEP, step.getResult().getNextAction());
	}

	@Test
	void clearsStartGameDialogBeforePersisting() {
		when(game.getDialogParameter()).thenReturn(new DialogStartGameParameter());

		new StepCheckpoint(gameState).start();

		InOrder order = inOrder(game, gameCache);
		order.verify(game).setDialogParameter(null);
		order.verify(gameCache).queueDbUpdate(gameState, true);
	}

	@Test
	void keepsOtherDialogs() {
		when(game.getDialogParameter()).thenReturn(new DialogBuyPrayersAndInducementsParameter());

		new StepCheckpoint(gameState).start();

		verify(game, never()).setDialogParameter(any());
	}
}
