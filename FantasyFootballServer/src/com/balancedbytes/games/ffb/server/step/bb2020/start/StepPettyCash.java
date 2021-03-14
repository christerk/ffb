package com.balancedbytes.games.ffb.server.step.bb2020.start;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.TeamResult;
import com.balancedbytes.games.ffb.report.ReportFreePettyCash;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;

/**
 * Step in start game sequence to handle petty cash.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepPettyCash extends AbstractStep {

	public StepPettyCash(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.PETTY_CASH;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}


	private void executeStep() {
		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();

		int availablePettyCash = game.getTeamHome().getTeamValue() - game.getTeamAway().getTeamValue();

		if (availablePettyCash != 0 ) {
			TeamResult underdogResult = availablePettyCash < 0 ? gameResult.getTeamResultHome() : gameResult.getTeamResultAway();

			underdogResult.setPettyCashTransferred(Math.abs(availablePettyCash));
			getResult().addReport(
				new ReportFreePettyCash(underdogResult.getTeam().getId(), underdogResult.getPettyCashTransferred()));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);

	}

}
