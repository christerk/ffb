package com.balancedbytes.games.ffb.server.step.game.end;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.report.ReportPenaltyShootout;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in end game sequence to handle the penalty shootout.
 * 
 * @author Kalimar
 */
public final class StepPenaltyShootout extends AbstractStep {

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
			int rollHome = 0, reRollsLeftHome = 0, penaltyScoreHome = 0;
			int rollAway = 0, reRollsLeftAway = 0, penaltyScoreAway = 0;
			while (penaltyScoreHome == penaltyScoreAway) {
				rollHome = getGameState().getDiceRoller().rollPenaltyShootout();
				reRollsLeftHome = game.getTurnDataHome().getReRolls();
				penaltyScoreHome = rollHome + reRollsLeftHome;
				rollAway = getGameState().getDiceRoller().rollPenaltyShootout();
				reRollsLeftAway = game.getTurnDataAway().getReRolls();
				penaltyScoreAway = rollAway + reRollsLeftAway;
			}
			if (penaltyScoreHome > penaltyScoreAway) {
				gameResult.getTeamResultHome().setScore(gameResult.getTeamResultHome().getScore() + 1);
			} else {
				gameResult.getTeamResultAway().setScore(gameResult.getTeamResultAway().getScore() + 1);
			}
			getResult().addReport(new ReportPenaltyShootout(rollHome, reRollsLeftHome, rollAway, reRollsLeftAway));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepPenaltyShootout initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		return this;
	}

}
