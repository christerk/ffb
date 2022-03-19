package com.fumbbl.ffb.server.step.bb2016.end;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.report.bb2016.ReportFanFactorRollPostMatch;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

/**
 * Step in end game sequence to handle fan factor changes.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
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
			fanFactorRollHome = getGameState().getDiceRoller().rollFanFactorPostMatch(scoreDiffHome > 0);
			fanFactorModifierHome = DiceInterpreter.getInstance().interpretFanFactorRoll(fanFactorRollHome,
					game.getTeamHome().getFanFactor(), scoreDiffHome);
		}
		gameResult.getTeamResultHome().setFanFactorModifier(fanFactorModifierHome);
		int[] fanFactorRollAway = null;
		int fanFactorModifierAway = -1;
		if (!gameResult.getTeamResultAway().hasConceded()) {
			fanFactorRollAway = getGameState().getDiceRoller().rollFanFactorPostMatch(scoreDiffHome < 0);
			fanFactorModifierAway = DiceInterpreter.getInstance().interpretFanFactorRoll(fanFactorRollAway,
					game.getTeamAway().getFanFactor(), -scoreDiffHome);
		}
		gameResult.getTeamResultAway().setFanFactorModifier(fanFactorModifierAway);
		getResult().addReport(
				new ReportFanFactorRollPostMatch(fanFactorRollHome, fanFactorModifierHome, fanFactorRollAway, fanFactorModifierAway));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepFanFactor initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		return this;
	}

}
