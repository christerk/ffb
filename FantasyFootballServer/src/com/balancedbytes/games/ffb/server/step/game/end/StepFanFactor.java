package com.balancedbytes.games.ffb.server.step.game.end;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.report.ReportFanFactorRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in end game sequence to handle fan factor changes.
 * 
 * @author Kalimar
 */
public final class StepFanFactor extends AbstractStep {

	public StepFanFactor(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.FAN_FACTOR;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		int scoreDiffHome = gameResult.getTeamResultHome().getScore() - gameResult.getTeamResultAway().getScore();
		int[] fanFactorRollHome = null;
		int fanFactorModifierHome = -1;
		if (!gameResult.getTeamResultHome().hasConceded()) {
			fanFactorRollHome = getGameState().getDiceRoller().rollFanFactor(scoreDiffHome > 0);
			fanFactorModifierHome = DiceInterpreter.getInstance().interpretFanFactorRoll(fanFactorRollHome,
					game.getTeamHome().getFanFactor(), scoreDiffHome);
		}
		gameResult.getTeamResultHome().setFanFactorModifier(fanFactorModifierHome);
		int[] fanFactorRollAway = null;
		int fanFactorModifierAway = -1;
		if (!gameResult.getTeamResultAway().hasConceded()) {
			fanFactorRollAway = getGameState().getDiceRoller().rollFanFactor(scoreDiffHome < 0);
			fanFactorModifierAway = DiceInterpreter.getInstance().interpretFanFactorRoll(fanFactorRollAway,
					game.getTeamAway().getFanFactor(), -scoreDiffHome);
		}
		gameResult.getTeamResultAway().setFanFactorModifier(fanFactorModifierAway);
		getResult().addReport(
				new ReportFanFactorRoll(fanFactorRollHome, fanFactorModifierHome, fanFactorRollAway, fanFactorModifierAway));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepFanFactor initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		return this;
	}

}
