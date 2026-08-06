package com.fumbbl.ffb.test;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Execution(ExecutionMode.CONCURRENT)
public class GameStateExecuteStepTest extends AbstractStateTest {

    private AtomicInteger logCurrentStepCount;

    @BeforeEach
    void initGameState() {
        gameState = testServer.getGameState();
        logCurrentStepCount = new AtomicInteger(0);
        DebugLog spiedLog = spy(testServer.getServer().getDebugLog());
        doAnswer(invocation -> {
            logCurrentStepCount.incrementAndGet();
            return null;
        }).when(spiedLog).logCurrentStep(anyInt(), any());
        testServer.getServer().setDebugLog(spiedLog);
    }

    private IStep createMockStep(StepAction action, String label) {
        IStep step = mock(IStep.class);
        StepResult result = new StepResult();
        result.setNextAction(action);
        result.setSynchronize(false);
        when(step.getResult()).thenReturn(result);
        when(step.getId()).thenReturn(StepId.INIT_SELECTING);
        when(step.getGameState()).thenReturn(gameState);
        when(step.getLabel()).thenReturn(label);
        when(step.getName()).thenReturn("TestStep");
        return step;
    }

    private IStep createMockStep(StepAction action) {
        return createMockStep(action, null);
    }

    private void pushSteps(IStep... steps) {
        for (int i = steps.length - 1; i >= 0; i--) {
            gameState.getStepStack().push(steps[i]);
        }
    }

    private ReceivedCommand newCommand() {
        return new ReceivedCommand(Commands.selectPlayer("test", PlayerAction.BLOCK), null);
    }

    @Test
    void startNextStep_emptyStack_setsCurrentStepNull() {
        gameState.startNextStep();
        assertNull(gameState.getCurrentStep());
    }

    @Test
    void startNextStep_stepReturnsContinue_callsStartOnly() {
        IStep step = createMockStep(StepAction.CONTINUE);
        pushSteps(step);

        gameState.startNextStep();

        verify(step).start();
        verify(step, never()).repeat();
        verify(step, never()).handleCommand(any());
    }

    @Test
    void startNextStep_stepReturnsRepeat_callsRepeatUntilContinue() {
        IStep step = mock(IStep.class);
        StepResult result = new StepResult();
        result.setNextAction(StepAction.REPEAT);
        result.setSynchronize(false);
        when(step.getResult()).thenReturn(result);
        when(step.getId()).thenReturn(StepId.INIT_SELECTING);
        when(step.getGameState()).thenReturn(gameState);

        AtomicInteger repeatCount = new AtomicInteger(0);
        doAnswer(invocation -> {
            if (repeatCount.incrementAndGet() >= 2) {
                result.setNextAction(StepAction.CONTINUE);
            }
            return null;
        }).when(step).repeat();

        pushSteps(step);
        gameState.startNextStep();

        verify(step).start();
        verify(step, times(2)).repeat();
    }

    @Test
    void startNextStep_stepReturnsMultipleRepeats_callsRepeatMultipleTimes() {
        IStep step = mock(IStep.class);
        StepResult result = new StepResult();
        result.setNextAction(StepAction.REPEAT);
        result.setSynchronize(false);
        when(step.getResult()).thenReturn(result);
        when(step.getId()).thenReturn(StepId.INIT_SELECTING);
        when(step.getGameState()).thenReturn(gameState);

        AtomicInteger repeatCount = new AtomicInteger(0);
        doAnswer(invocation -> {
            if (repeatCount.incrementAndGet() >= 5) {
                result.setNextAction(StepAction.CONTINUE);
            }
            return null;
        }).when(step).repeat();

        pushSteps(step);
        gameState.startNextStep();

        verify(step).start();
        verify(step, times(5)).repeat();
    }

    @Test
    void startNextStep_stepReturnsNextStep_startsNextStepFromStack() {
        IStep step2 = createMockStep(StepAction.CONTINUE);
        IStep step1 = createMockStep(StepAction.NEXT_STEP);

        pushSteps(step1, step2);

        gameState.startNextStep();

        verify(step1).start();
        verify(step1, never()).repeat();
        verify(step2).start();
        verify(step2, never()).repeat();
    }

    @Test
    void startNextStep_stepReturnsNextStepAndRepeat_forwardsToNextStepWithoutCallingStart() {
        IStep step2 = createMockStep(StepAction.CONTINUE);
        IStep step1 = createMockStep(StepAction.NEXT_STEP_AND_REPEAT);

        pushSteps(step1, step2);

        gameState.startNextStep();

        verify(step1).start();
        verify(step1, never()).repeat();
        verify(step2, never()).start();
        verify(step2, never()).repeat();
        verify(step2, never()).handleCommand(any());
    }

    @Test
    void startNextStep_stepReturnsNextStepAndRepeat_loopContinuesWithHandleCommandMode() {
        IStep step2 = mock(IStep.class);
        StepResult result2 = new StepResult();
        result2.setNextAction(StepAction.NEXT_STEP_AND_REPEAT);
        result2.setSynchronize(false);
        when(step2.getResult()).thenReturn(result2);
        when(step2.getId()).thenReturn(StepId.INIT_SELECTING);
        when(step2.getGameState()).thenReturn(gameState);

        IStep step3 = createMockStep(StepAction.CONTINUE);

        IStep step1 = createMockStep(StepAction.NEXT_STEP_AND_REPEAT);

        pushSteps(step1, step2, step3);

        gameState.startNextStep();

        verify(step1).start();
        verify(step1, never()).repeat();
        verify(step2, never()).start();
        verify(step2, never()).repeat();
        verify(step2, never()).handleCommand(any());
        verify(step3, never()).start();
        verify(step3, never()).repeat();
        verify(step3, never()).handleCommand(any());
    }

    @Test
    void startNextStep_stepReturnsGotoLabel_goesToLabelAndStartsNextStep() {
        IStep targetStep = createMockStep(StepAction.CONTINUE, "target");
        IStep intermediateStep = createMockStep(StepAction.CONTINUE);
        IStep step1 = createMockStep(StepAction.GOTO_LABEL);

        StepResult result1 = step1.getResult();
        result1.setNextAction(StepAction.GOTO_LABEL, "target");

        pushSteps(step1, intermediateStep, targetStep);

        gameState.startNextStep();

        verify(step1).start();
        verify(targetStep).start();
        verify(intermediateStep, never()).start();
    }

    @Test
    void startNextStep_stepReturnsGotoLabelAndRepeat_goesToLabelAndForwards() {
        IStep finalStep = createMockStep(StepAction.CONTINUE);

        IStep targetStep = mock(IStep.class);
        StepResult targetResult = new StepResult();
        targetResult.setNextAction(StepAction.NEXT_STEP_AND_REPEAT);
        targetResult.setSynchronize(false);
        when(targetStep.getResult()).thenReturn(targetResult);
        when(targetStep.getId()).thenReturn(StepId.INIT_SELECTING);
        when(targetStep.getGameState()).thenReturn(gameState);
        when(targetStep.getLabel()).thenReturn("target");
        when(targetStep.getName()).thenReturn("TargetStep");

        IStep intermediateStep = createMockStep(StepAction.CONTINUE);
        IStep step1 = createMockStep(StepAction.GOTO_LABEL_AND_REPEAT);

        StepResult result1 = step1.getResult();
        result1.setNextAction(StepAction.GOTO_LABEL_AND_REPEAT, "target");

        pushSteps(step1, intermediateStep, targetStep, finalStep);

        gameState.startNextStep();

        verify(step1).start();
        verify(targetStep, never()).start();
        verify(targetStep, never()).repeat();
        verify(targetStep, never()).handleCommand(any());
        verify(intermediateStep, never()).start();
        verify(finalStep, never()).start();
    }

    @Test
    void handleCommand_withCommandAndCurrentStep_callsHandleCommandOnStep() {
        IStep step2 = createMockStep(StepAction.CONTINUE);
        IStep step1 = createMockStep(StepAction.NEXT_STEP);

        pushSteps(step1, step2);

        gameState.startNextStep();

        ReceivedCommand cmd = newCommand();
        gameState.handleCommand(cmd);

        verify(step2).start();
        verify(step2).handleCommand(cmd);
    }

    @Test
    void handleCommand_nullCommand_returnsImmediately() {
        IStep step = createMockStep(StepAction.CONTINUE);
        pushSteps(step);
        gameState.startNextStep();

        gameState.handleCommand(null);

        verify(step).start();
        verify(step, never()).handleCommand(any());
    }

    @Test
    void handleCommand_currentStepNull_callsStartNextStepFirst() {
        IStep step = createMockStep(StepAction.CONTINUE);
        pushSteps(step);

        assertNull(gameState.getCurrentStep());

        ReceivedCommand cmd = newCommand();
        gameState.handleCommand(cmd);

        verify(step).start();
        verify(step).handleCommand(cmd);
    }

    @Test
    void handleCommand_withCurrentStepNull_startsNextStepThenHandlesCommandOnResultingStep() {
        IStep step2 = createMockStep(StepAction.CONTINUE);
        IStep step1 = createMockStep(StepAction.NEXT_STEP);

        pushSteps(step1, step2);

        ReceivedCommand cmd = newCommand();
        gameState.handleCommand(cmd);

        verify(step1).start();
        verify(step1, never()).handleCommand(any());
        verify(step2).start();
        verify(step2).handleCommand(cmd);
    }

    @Test
    void forwardPath_nextStepNull_throwsNullPointerException() {
        IStep step = createMockStep(StepAction.NEXT_STEP_AND_REPEAT);

        pushSteps(step);

        assertThrows(NullPointerException.class, () -> gameState.startNextStep());
    }

    @Test
    void gotoLabel_nullLabel_throwsStepException() {
        IStep step = createMockStep(StepAction.GOTO_LABEL);

        StepResult result = step.getResult();
        result.setNextAction(StepAction.GOTO_LABEL, null);

        pushSteps(step);

        assertThrows(StepException.class, () -> gameState.startNextStep());
    }

    @Test
    void gotoLabel_labelNotFound_throwsStepException() {
        IStep step = createMockStep(StepAction.GOTO_LABEL);

        StepResult result = step.getResult();
        result.setNextAction(StepAction.GOTO_LABEL, "nonexistent");

        pushSteps(step);

        assertThrows(StepException.class, () -> gameState.startNextStep());
    }

    @Test
    void startNextStep_stepReturnsRepeatOnce_thenContinue() {
        IStep step = mock(IStep.class);
        StepResult result = new StepResult();
        result.setNextAction(StepAction.REPEAT);
        result.setSynchronize(false);
        when(step.getResult()).thenReturn(result);
        when(step.getId()).thenReturn(StepId.INIT_SELECTING);
        when(step.getGameState()).thenReturn(gameState);

        AtomicInteger repeatCount = new AtomicInteger(0);
        doAnswer(invocation -> {
            if (repeatCount.incrementAndGet() >= 1) {
                result.setNextAction(StepAction.CONTINUE);
            }
            return null;
        }).when(step).repeat();

        pushSteps(step);
        gameState.startNextStep();

        verify(step).start();
        verify(step, times(1)).repeat();
    }

    @Test
    void handleCommand_thenStartNextStep_thenHandleCommandAgain_fullyCycles() {
        IStep step3 = createMockStep(StepAction.CONTINUE);
        IStep step2 = createMockStep(StepAction.NEXT_STEP);
        IStep step1 = createMockStep(StepAction.NEXT_STEP);

        pushSteps(step1, step2, step3);

        gameState.startNextStep();

        ReceivedCommand cmd1 = newCommand();
        gameState.handleCommand(cmd1);

        ReceivedCommand cmd2 = newCommand();
        gameState.handleCommand(cmd2);

        verify(step1).start();
        verify(step2).start();
        verify(step3).start();

        verify(step3).handleCommand(cmd1);
        verify(step3).handleCommand(cmd2);
    }

    @Test
    void processStepResult_afterStart_nextActionIsContinue_currentStepPreserved() {
        IStep step = createMockStep(StepAction.CONTINUE);
        pushSteps(step);

        gameState.startNextStep();

        assertNotNull(gameState.getCurrentStep());
        assertEquals(StepAction.CONTINUE, gameState.getCurrentStep().getResult().getNextAction());
    }

    @Test
    void stepIdAndName_accessible_duringExceptionHandling() {
        IStep step = createMockStep(StepAction.GOTO_LABEL);

        StepResult result = step.getResult();
        result.setNextAction(StepAction.GOTO_LABEL, "missingLabel");

        pushSteps(step);

        StepException ex = assertThrows(StepException.class, () -> gameState.startNextStep());

        assertTrue(ex.getMessage().contains("Goto unknown label missingLabel"));
    }

    @Test
    void startNextStep_noExceptionWhenStackHasMultipleSteps_withContinueChain() {
        IStep step3 = createMockStep(StepAction.CONTINUE);
        IStep step2 = createMockStep(StepAction.CONTINUE);
        IStep step1 = createMockStep(StepAction.NEXT_STEP);

        pushSteps(step1, step2, step3);

        assertDoesNotThrow(() -> gameState.startNextStep());

        verify(step1).start();
        verify(step2).start();
        verify(step3, never()).start();
    }

    @Test
    void forwardCommand_afterStart_doesNotCallHandleCommandOnNextStep() {
        IStep step2 = mock(IStep.class);
        StepResult result2 = new StepResult();
        result2.setNextAction(StepAction.NEXT_STEP_AND_REPEAT);
        result2.setSynchronize(false);
        when(step2.getResult()).thenReturn(result2);
        when(step2.getId()).thenReturn(StepId.INIT_SELECTING);
        when(step2.getGameState()).thenReturn(gameState);

        IStep step3 = createMockStep(StepAction.CONTINUE);
        IStep step1 = createMockStep(StepAction.NEXT_STEP_AND_REPEAT);

        IStep step4 = createMockStep(StepAction.CONTINUE);

        pushSteps(step1, step2, step3, step4);

        gameState.startNextStep();

        verify(step1).start();
        verify(step1, never()).handleCommand(any());
        verify(step2, never()).start();
        verify(step2, never()).handleCommand(any());
        verify(step3, never()).start();
        verify(step3, never()).handleCommand(any());
        verify(step4, never()).start();
        verify(step4, never()).handleCommand(any());
    }

    @Test
    void gotoLabel_withExistingLabel_noFurtherStepsOnStack_stopsAfterGoTo() {
        IStep targetStep = createMockStep(StepAction.CONTINUE, "targetLabel");
        IStep step1 = createMockStep(StepAction.GOTO_LABEL);

        StepResult result1 = step1.getResult();
        result1.setNextAction(StepAction.GOTO_LABEL, "targetLabel");

        pushSteps(step1, targetStep);

        gameState.startNextStep();

        verify(step1).start();
        verify(targetStep).start();
    }

    @Test
    void startNextStep_nextStep_emptyStack_onStartTransition_returnsCleanly() {
        IStep step = createMockStep(StepAction.NEXT_STEP);
        pushSteps(step);

        assertDoesNotThrow(() -> gameState.startNextStep());

        verify(step).start();
    }

    @Test
    void gotoLabelAndRepeat_targetReturnsNextStep_triggersStartTransitionAfterForward() {
        IStep afterStep = createMockStep(StepAction.CONTINUE);

        IStep targetStep = createMockStep(StepAction.NEXT_STEP, "target");
        IStep intermediateStep = createMockStep(StepAction.CONTINUE);
        IStep step1 = createMockStep(StepAction.GOTO_LABEL_AND_REPEAT);

        StepResult result1 = step1.getResult();
        result1.setNextAction(StepAction.GOTO_LABEL_AND_REPEAT, "target");

        pushSteps(step1, intermediateStep, targetStep, afterStep);

        gameState.startNextStep();

        verify(step1).start();
        verify(targetStep, never()).start();
        verify(intermediateStep, never()).start();
        verify(afterStep).start();
    }

    @Test
    void forwardCommand_thenStartTransition_chainsWithoutCallingStartOnMiddleStep() {
        IStep step3 = createMockStep(StepAction.CONTINUE);
        IStep step2 = createMockStep(StepAction.NEXT_STEP);
        IStep step1 = createMockStep(StepAction.NEXT_STEP_AND_REPEAT);

        pushSteps(step1, step2, step3);

        gameState.startNextStep();

        verify(step1).start();
        verify(step2, never()).start();
        verify(step2, never()).handleCommand(any());
        verify(step3).start();
    }

    @Test
    void handleCommand_stepReturnsNextStepAfterCommand_triggersStartTransition() {
        IStep step2 = createMockStep(StepAction.CONTINUE);

        IStep step1 = mock(IStep.class);
        StepResult result1 = new StepResult();
        result1.setNextAction(StepAction.CONTINUE);
        result1.setSynchronize(false);
        when(step1.getResult()).thenReturn(result1);
        when(step1.getId()).thenReturn(StepId.INIT_SELECTING);
        when(step1.getGameState()).thenReturn(gameState);
        doAnswer(invocation -> {
            result1.setNextAction(StepAction.NEXT_STEP);
            return null;
        }).when(step1).handleCommand(any());

        pushSteps(step1, step2);

        gameState.startNextStep();

        ReceivedCommand cmd = newCommand();
        gameState.handleCommand(cmd);

        verify(step1).start();
        verify(step1).handleCommand(cmd);
        verify(step2).start();
        verify(step2, never()).handleCommand(any());
    }

    @Test
    void handleCommand_stepReturnsNextStep_triggersStartTransitionInHandleCommandPath() {
        IStep step3 = createMockStep(StepAction.CONTINUE);
        IStep step2 = createMockStep(StepAction.NEXT_STEP);
        IStep step1 = createMockStep(StepAction.NEXT_STEP);

        pushSteps(step1, step2, step3);

        ReceivedCommand cmd = newCommand();
        gameState.handleCommand(cmd);

        verify(step1).start();
        verify(step1, never()).handleCommand(any());
        verify(step2).start();
        verify(step2, never()).handleCommand(any());
        verify(step3).start();
        verify(step3).handleCommand(cmd);
    }

    @Test
    void logging_startNextStepAndStartTransition_logCurrentStep_forwardDoesNot() {
        IStep step5 = createMockStep(StepAction.CONTINUE);
        IStep step4 = createMockStep(StepAction.NEXT_STEP);
        IStep step3 = createMockStep(StepAction.NEXT_STEP_AND_REPEAT);
        IStep step2 = createMockStep(StepAction.NEXT_STEP);
        IStep step1 = createMockStep(StepAction.NEXT_STEP_AND_REPEAT);

        pushSteps(step1, step2, step3, step4, step5);

        gameState.startNextStep();

        assertEquals(3, logCurrentStepCount.get(),
                "Expected 3 log calls: startNextStep(step1) + START(step3) + START(step5). "
                        + "FORWARD steps (step2, step4) should not log.");
    }

    @Test
    void logging_multipleStartTransitions_eachLogs() {
        IStep step3 = createMockStep(StepAction.CONTINUE);
        IStep step2 = createMockStep(StepAction.NEXT_STEP);
        IStep step1 = createMockStep(StepAction.NEXT_STEP);

        pushSteps(step1, step2, step3);

        gameState.startNextStep();

        assertEquals(3, logCurrentStepCount.get(),
                "Expected 3 log calls: startNextStep(step1) + START(step2) + START(step3)");
    }

    @Test
    void logging_forwardOnlyChain_onlyStartNextStepLogs() {
        IStep step3 = createMockStep(StepAction.CONTINUE);
        IStep step2 = createMockStep(StepAction.NEXT_STEP_AND_REPEAT);
        IStep step1 = createMockStep(StepAction.NEXT_STEP_AND_REPEAT);

        pushSteps(step1, step2, step3);

        gameState.startNextStep();

        assertEquals(1, logCurrentStepCount.get(),
                "Expected 1 log call from startNextStep(step1). "
                        + "FORWARD steps (step2, step3) should not log.");
    }

    @Test
    void logging_handleCommand_entryPoint_doesNotLog() {
        IStep step2 = createMockStep(StepAction.CONTINUE);
        IStep step1 = createMockStep(StepAction.NEXT_STEP);

        pushSteps(step1, step2);
        gameState.startNextStep();
        int logCountAfterStart = logCurrentStepCount.get();

        gameState.handleCommand(newCommand());
        assertEquals(logCountAfterStart, logCurrentStepCount.get(),
                "handleCommand entry point should not call logCurrentStep");
    }

    @Test
    void loggingCommand_startNextStepLogs_evenWhenEnteredFromHandleCommand() {
        IStep step1 = createMockStep(StepAction.CONTINUE);
        pushSteps(step1);

        int logCountBefore = logCurrentStepCount.get();
        gameState.handleCommand(newCommand());

        assertEquals(logCountBefore + 1, logCurrentStepCount.get(),
                "handleCommand with null fCurrentStep calls startNextStep which logs once");
    }

    @Test
    void repeatAfterHandleCommand_doesNotCallStartAgain() {
        IStep step2 = mock(IStep.class);
        StepResult result2 = new StepResult();
        result2.setNextAction(StepAction.REPEAT);
        result2.setSynchronize(false);
        when(step2.getResult()).thenReturn(result2);
        when(step2.getId()).thenReturn(StepId.INIT_SELECTING);
        when(step2.getGameState()).thenReturn(gameState);

        AtomicInteger repeatCount = new AtomicInteger(0);
        doAnswer(invocation -> {
            if (repeatCount.incrementAndGet() >= 2) {
                result2.setNextAction(StepAction.CONTINUE);
            }
            return null;
        }).when(step2).repeat();

        IStep step1 = createMockStep(StepAction.NEXT_STEP);

        pushSteps(step1, step2);

        gameState.startNextStep();

        ReceivedCommand cmd = newCommand();
        gameState.handleCommand(cmd);

        verify(step2, times(1)).start();
        verify(step2, times(1)).handleCommand(cmd);
        verify(step2, times(2)).repeat();
    }
}
