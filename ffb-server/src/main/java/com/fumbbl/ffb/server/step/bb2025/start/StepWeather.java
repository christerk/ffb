package com.fumbbl.ffb.server.step.bb2025.start;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.report.ReportWeather;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;

/**
 * Step in start game sequence to roll weather.
 * <p>
 * Fans and weather are rolled at this point, so the game state is persisted as a recovery point. The next action is
 * set before the game state is persisted, so a game restored from this snapshot resumes with the following step
 * (see {@link GameState#resumeFinishedStep()}). Persisting therefore has to remain the last thing this step does.
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepWeather extends AbstractStep {

	public StepWeather(GameState gameState) {
		super(gameState);
	}

	public StepId getId() {
		return StepId.WEATHER;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand receivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(receivedCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		getResult().addReport(rollWeather());
		getResult().setNextAction(StepAction.NEXT_STEP);
		persistRecoveryPoint();
	}

	private ReportWeather rollWeather() {
		int[] roll = getGameState().getDiceRoller().rollWeather();
		Weather weather = DiceInterpreter.getInstance().interpretRollWeather(roll);
		getGameState().getGame().getFieldModel().setWeather(weather);
		return new ReportWeather(weather, roll);
	}

	private void persistRecoveryPoint() {
		Game game = getGameState().getGame();
		// both coaches have already started the game, a restored game must not ask them again
		if ((game.getDialogParameter() != null) && (game.getDialogParameter().getId() == DialogId.START_GAME)) {
			game.setDialogParameter(null);
		}
		getGameState().getServer().getGameCache().queueDbUpdate(getGameState(), true);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepWeather initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		return this;
	}

}
