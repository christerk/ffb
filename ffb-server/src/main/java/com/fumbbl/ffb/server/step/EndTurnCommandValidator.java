package com.fumbbl.ffb.server.step;

import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.net.commands.ClientCommandEndTurn;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;

import java.util.Arrays;

/**
 * Validates end turn commands to protect steps against duplicate or stale commands, e.g. caused by a double clicked
 * end turn button whose second command arrives after the server already moved on to the next step.
 */
public class EndTurnCommandValidator {

	public boolean isValid(GameState gameState, ReceivedCommand receivedCommand, TurnMode... expectedTurnModes) {
		return isExpectedTurnMode(gameState, expectedTurnModes)
			&& matchesTurnMode(gameState, receivedCommand)
			&& UtilServerSteps.checkCommandIsFromCurrentPlayer(gameState, receivedCommand);
	}

	// the game has to be in one of the turn modes the step actually waits for an end turn in, otherwise the command
	// belongs to an earlier phase and would end the upcoming one before the coach had any chance to act
	private boolean isExpectedTurnMode(GameState gameState, TurnMode... expectedTurnModes) {
		return expectedTurnModes.length == 0
			|| Arrays.asList(expectedTurnModes).contains(gameState.getGame().getTurnMode());
	}

	// commands of older clients do not carry a turn mode, those can only be checked against the expected turn modes
	private boolean matchesTurnMode(GameState gameState, ReceivedCommand receivedCommand) {
		TurnMode commandTurnMode = ((ClientCommandEndTurn) receivedCommand.getCommand()).getTurnMode();
		return commandTurnMode == null || commandTurnMode == gameState.getGame().getTurnMode();
	}
}
