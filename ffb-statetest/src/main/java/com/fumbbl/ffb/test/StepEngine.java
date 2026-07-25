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
		return gameState.getCurrentStep();
	}
}
