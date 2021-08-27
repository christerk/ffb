package com.fumbbl.ffb.server.step.bb2020.end;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.report.bb2020.ReportPenaltyShootout;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

/**
 * Step in end game sequence to handle the penalty shootout.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepPenaltyShootout extends AbstractStep {

	private static final int SHOOTOUT_LIMIT = 5;

	public StepPenaltyShootout(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.PENALTY_SHOOTOUT;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		if ((game.getHalf() > 2)
				&& (gameResult.getTeamResultHome().getScore() == gameResult.getTeamResultAway().getScore())) {
			int rollHome, penaltyScoreHome = 0;
			int rollAway, penaltyScoreAway = 0;
			while (penaltyScoreHome + penaltyScoreAway < SHOOTOUT_LIMIT) {
				int currentPenalty = penaltyScoreAway + penaltyScoreHome + 1;
				rollHome = getGameState().getDiceRoller().rollPenaltyShootout();
				rollAway = getGameState().getDiceRoller().rollPenaltyShootout();
				Boolean homeTeamWonPenalty = null;
				if (rollAway > rollHome) {
					homeTeamWonPenalty = false;
					penaltyScoreAway++;
				} else if (rollAway < rollHome) {
					homeTeamWonPenalty = true;
					penaltyScoreHome++;
				}

				String teamId = null;

				if (penaltyScoreHome + penaltyScoreAway == SHOOTOUT_LIMIT) {
					gameResult.getTeamResultHome().setPenaltyScore(penaltyScoreHome);
					gameResult.getTeamResultAway().setPenaltyScore(penaltyScoreAway);
					if (penaltyScoreHome > penaltyScoreAway) {
						teamId = game.getTeamHome().getId();
					} else {
						teamId = game.getTeamAway().getId();
					}
				}

				getResult().addReport(new ReportPenaltyShootout(rollHome, penaltyScoreHome, rollAway, penaltyScoreAway, homeTeamWonPenalty, toOrdinal(currentPenalty), teamId));
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private String toOrdinal(int number) {
		switch (number) {
			case 1:
					return "1st";
			case 2:
				return "2nd";
			case 3:
				return "3rd";
			default:
				return number + "th";
		}
	}

}
