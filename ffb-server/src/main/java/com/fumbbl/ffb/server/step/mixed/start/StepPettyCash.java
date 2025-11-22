package com.fumbbl.ffb.server.step.bb2020.start;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.mixed.ReportFreePettyCash;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

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

		gameResult.getTeamResultHome()
			.setTeamValue(Math.max(gameResult.getTeamResultHome().getTeamValue(), game.getTeamHome().getTeamValue()));
		gameResult.getTeamResultAway()
			.setTeamValue(Math.max(gameResult.getTeamResultAway().getTeamValue(), game.getTeamAway().getTeamValue()));

		if (availablePettyCash != 0) {
			TeamResult underdogResult = availablePettyCash < 0 ? gameResult.getTeamResultHome() : gameResult.getTeamResultAway();

			// we set this value always to know who should go first in the inducement step even if petty cash will not be used
			underdogResult.setPettyCashFromTvDiff(Math.abs(availablePettyCash));
			if (!UtilGameOption.isOptionEnabled(game, GameOptionId.INDUCEMENTS_ALWAYS_USE_TREASURY)) {

				getResult().addReport(
					new ReportFreePettyCash(underdogResult.getTeam().getId(), underdogResult.getPettyCashFromTvDiff()));
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);

	}

}
