package com.fumbbl.ffb.server.step.bb2020.inducements;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogSelectWeatherParameter;
import com.fumbbl.ffb.report.bb2020.ReportWeatherMageRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.util.UtilServerDialog;

import java.util.HashMap;
import java.util.Map;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepWeatherMage extends AbstractStep {

	public StepWeatherMage(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.WEATHER_MAGE;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		int[] roll = getGameState().getDiceRoller().rollWeather();
		int sum = roll[0] + roll[1];
		Weather weather = DiceInterpreter.getInstance().interpretWeather(sum);
		Map<String, Integer> weatherOptions = new HashMap<>();
		weatherOptions.put(weather.name(), 0);

		for (int i = 1; i < 3; i++) {
			addWeather(weatherOptions, sum + i);
			addWeather(weatherOptions, sum - i);
		}

		getResult().addReport(new ReportWeatherMageRoll(roll));

		UtilServerDialog.showDialog(getGameState(), new DialogSelectWeatherParameter(weatherOptions), false);

	}

	private void addWeather(Map<String, Integer> weatherOptions, int sum) {
		String weatherName = DiceInterpreter.getInstance().interpretWeather(Math.min(12, Math.max(2, sum))).name();
		if (!weatherOptions.containsKey(weatherName)) {
			weatherOptions.put(weatherName, sum);
		}
	}

	private void replaceWeather(Weather weather) {
	}
}
