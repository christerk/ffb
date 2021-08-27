package com.fumbbl.ffb.server.step.game.start;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.IFactorySource;
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
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepWeather extends AbstractStep {

	public StepWeather(GameState pGameState) {
		super(pGameState);
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
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		getResult().addReport(rollWeather());
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private ReportWeather rollWeather() {
		int[] roll = getGameState().getDiceRoller().rollWeather();
		Weather weather = DiceInterpreter.getInstance().interpretRollWeather(roll);
		getGameState().getGame().getFieldModel().setWeather(weather);
		return new ReportWeather(weather, roll);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepWeather initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		return this;
	}

}
