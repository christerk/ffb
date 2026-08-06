package com.fumbbl.ffb.test;

import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.IStep;

public class StepEngine {
    public static IStep start(GameState gameState) {
        gameState.startNextStep();
        return gameState.getCurrentStep();
    }

    public static IStep respond(GameState gameState, NetCommand command) {
        gameState.handleCommand(new ReceivedCommand(command, null));
        clearRollFlagIfAllConsumed(gameState);
        return gameState.getCurrentStep();
    }

	private static void clearRollFlagIfAllConsumed(GameState gameState) {
		if (gameState.getDiceRoller() instanceof TestDiceRoller) {
			TestDiceRoller testDiceRoller = (TestDiceRoller) gameState.getDiceRoller();
			if (testDiceRoller.allRollsConsumed()) {
				TestFortuna.clearTestRollsFlag();
			}
		}
	}
}
