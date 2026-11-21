package com.fumbbl.ffb.server;

import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepResult;
import com.fumbbl.ffb.server.step.StepStack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StepExecutorTest {

	private GameState gameState;
	private StepStack stepStack;
	private StepExecutor executor;

	@BeforeEach
	void setUp() {
		gameState = mock(GameState.class);
		FantasyFootballServer server = mock(FantasyFootballServer.class);
		when(gameState.getServer()).thenReturn(server);
		when(server.getDebugLog()).thenReturn(mock(DebugLog.class));
		stepStack = new StepStack(gameState);
		when(gameState.getStepStack()).thenReturn(stepStack);
		executor = new StepExecutor(gameState);
	}

	@Test
	void resumeFinishedStepStartsNextStepWhenRestoredStepHadFinished() {
		IStep checkpoint = step(StepAction.NEXT_STEP);
		IStep next = step(StepAction.CONTINUE);
		stepStack.push(next);
		executor.setCurrentStep(checkpoint);

		assertTrue(executor.resumeFinishedStep());

		verify(next).start();
		assertSame(next, executor.getCurrentStep());
		assertNull(stepStack.peek());
	}

	@Test
	void resumeFinishedStepKeepsWaitingStep() {
		IStep waiting = step(StepAction.CONTINUE);
		IStep next = step(StepAction.CONTINUE);
		stepStack.push(next);
		executor.setCurrentStep(waiting);

		assertFalse(executor.resumeFinishedStep());

		verify(next, never()).start();
		assertSame(waiting, executor.getCurrentStep());
		assertSame(next, stepStack.peek());
	}

	@Test
	void resumeFinishedStepDoesNothingWithoutCurrentStep() {
		IStep next = step(StepAction.CONTINUE);
		stepStack.push(next);

		assertFalse(executor.resumeFinishedStep());

		verify(next, never()).start();
		assertSame(next, stepStack.peek());
	}

	private IStep step(StepAction nextAction) {
		IStep step = mock(IStep.class);
		StepResult result = new StepResult();
		result.setNextAction(nextAction);
		result.setSynchronize(false);
		when(step.getResult()).thenReturn(result);
		return step;
	}
}
