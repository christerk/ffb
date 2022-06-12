package com.fumbbl.ffb.server.step.bb2020.inducements;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepId;

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
		Weather weather = DiceInterpreter.getInstance().interpretRollWeather(roll);

	}

	private void replaceWeather(Weather weather) {
	}
}
