package com.fumbbl.ffb.server.step.bb2020.end;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
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
				rollHome = getGameState().getDiceRoller().rollPenaltyShootout();
				rollAway = getGameState().getDiceRoller().rollPenaltyShootout();
				String winningCoach = null;
				if (rollAway > rollHome) {
					winningCoach = game.getTeamAway().getCoach();
					penaltyScoreAway++;
				} else if (rollAway < rollHome) {
					winningCoach = game.getTeamHome().getCoach();
					penaltyScoreHome++;
				}

				String teamId = null;

				if (penaltyScoreHome + penaltyScoreAway == SHOOTOUT_LIMIT) {
					if (penaltyScoreHome > penaltyScoreAway) {
						gameResult.getTeamResultHome().setScore(gameResult.getTeamResultHome().getScore() + 1);
						teamId = game.getTeamHome().getId();
					} else {
						gameResult.getTeamResultAway().setScore(gameResult.getTeamResultAway().getScore() + 1);
						teamId = game.getTeamAway().getId();
					}
				}

				getResult().addReport(new ReportPenaltyShootout(rollHome, penaltyScoreHome, rollAway, penaltyScoreAway, winningCoach, toOrdinal(penaltyScoreAway + penaltyScoreHome), teamId));
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepPenaltyShootout initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		return this;
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
