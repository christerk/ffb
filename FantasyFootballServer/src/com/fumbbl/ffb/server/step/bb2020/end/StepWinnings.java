package com.fumbbl.ffb.server.step.bb2020.end;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;

/**
 * Step in end game sequence to roll winnings.
 * 
 * Needs to be initialized with stepParameter ADMIN_MODE.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepWinnings extends AbstractStep {

	public StepWinnings(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.WINNINGS;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		GameResult gameResult = getGameState().getGame().getGameResult();
		double attendance = gameResult.getTeamResultAway().getFanFactor() + gameResult.getTeamResultHome().getFanFactor();
		double homeWinnings = gameResult.getTeamResultHome().getScore();
		double awayWinnings = gameResult.getTeamResultAway().getScore();
		
		if (gameResult.getTeamResultHome().hasConceded()) {
			awayWinnings += attendance;
		} else if (gameResult.getTeamResultAway().hasConceded()) {
			homeWinnings += attendance;
		} else {
			awayWinnings += attendance / 2;
			homeWinnings += attendance / 2;
		}

		homeWinnings *= 10000;
		awayWinnings *= 10000;

		gameResult.getTeamResultAway().setWinnings((int) awayWinnings);
		gameResult.getTeamResultHome().setWinnings((int) homeWinnings);

		getResult().setNextAction(StepAction.NEXT_STEP);

	}
}
