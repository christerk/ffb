package com.fumbbl.ffb.test;

import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.IStep;
import org.eclipse.jetty.websocket.api.Session;

public class StepEngine {
	public static IStep start(GameState gameState) {
		gameState.startNextStep();
		return gameState.getCurrentStep();
	}

	public static IStep respond(GameState gameState, NetCommand command) {
		return respond(gameState, command, null);
	}

	public static IStep respond(GameState gameState, NetCommand command, Session session) {
		gameState.handleCommand(new ReceivedCommand(command, session));
		return gameState.getCurrentStep();
	}
}
