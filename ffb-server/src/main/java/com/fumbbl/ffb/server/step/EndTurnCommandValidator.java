package com.fumbbl.ffb.server.step;

import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.net.commands.ClientCommandEndTurn;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;

/**
 * Validates end turn commands to protect steps against duplicate or stale commands, e.g. caused by a double clicked
 * end turn button whose second command arrives after the server already moved on to the next step.
 */
public class EndTurnCommandValidator {

	public boolean isValid(GameState gameState, ReceivedCommand receivedCommand) {
		return matchesTurnMode(gameState, receivedCommand)
			&& UtilServerSteps.checkCommandIsFromCurrentPlayer(gameState, receivedCommand);
	}

	public boolean matchesTurnMode(GameState gameState, ReceivedCommand receivedCommand) {
		TurnMode commandTurnMode = ((ClientCommandEndTurn) receivedCommand.getCommand()).getTurnMode();
		return commandTurnMode == null || commandTurnMode == gameState.getGame().getTurnMode();
	}
}
