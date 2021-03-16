package com.balancedbytes.games.ffb.server.step.bb2020.start;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.TeamResult;
import com.balancedbytes.games.ffb.report.ReportFanFactor;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in start game sequence to roll spectators.
 *
 * Updates persistence. Pushes kickoffSequence on stack when finishing.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
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
	public StepSpectators initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		return this;
	}

}
