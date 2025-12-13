package com.fumbbl.ffb.server.step.mixed.start;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.report.mixed.ReportFanFactor;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;

/**
 * Step in start game sequence to roll spectators.
 * <p>
 * Updates persistence.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepSpectators extends AbstractStep {

	public StepSpectators(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.SPECTATORS;
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
		rollSpectators();
		GameCache gameCache = getGameState().getServer().getGameCache();
		gameCache.queueDbUpdate(getGameState(), true);
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void rollSpectators() {
		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		TeamResult teamResultHome = gameResult.getTeamResultHome();
		TeamResult teamResultAway = gameResult.getTeamResultAway();

		int fanRollHome = getGameState().getDiceRoller().rollFanFactor();
		teamResultHome.setFanFactor(game.getTeamHome().getDedicatedFans() + fanRollHome);

		int fanRollAway = getGameState().getDiceRoller().rollFanFactor();
		teamResultAway.setFanFactor(game.getTeamAway().getDedicatedFans() + fanRollAway);

		getResult().addReport(new ReportFanFactor(game.getTeamHome().getId(), fanRollHome, game.getTeamHome().getDedicatedFans()));
		getResult().addReport(new ReportFanFactor(game.getTeamAway().getId(), fanRollAway, game.getTeamAway().getDedicatedFans()));
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepSpectators initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		return this;
	}

}
