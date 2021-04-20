package com.fumbbl.ffb.server.step.game.end;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.report.ReportPenaltyShootout;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

/**
 * Step in end game sequence to handle the penalty shootout.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
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
	public StepPenaltyShootout initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		return this;
	}

}
