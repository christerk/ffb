package com.fumbbl.ffb.server.step.bb2020.inducements;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogInformationOkayParameter;
import com.fumbbl.ffb.dialog.DialogSelectWeatherParameter;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.net.commands.ClientCommandSelectWeather;
import com.fumbbl.ffb.report.bb2020.ReportWeatherMageResult;
import com.fumbbl.ffb.report.bb2020.ReportWeatherMageRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepWeatherMage extends AbstractStep {

	public static final String DIALOG_TITLE = "Weather Mage";

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

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);

		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_SELECT_WEATHER:
					ClientCommandSelectWeather clientCommandSelectWeather = (ClientCommandSelectWeather) pReceivedCommand.getCommand();

					replaceWeather(Weather.valueOf(clientCommandSelectWeather.getWeatherName()), clientCommandSelectWeather.getModifier(), ReportWeatherMageResult.Effect.CHANGED);
					getResult().setNextAction(StepAction.NEXT_STEP);
					commandStatus = StepCommandStatus.SKIP_STEP;
					break;
				case CLIENT_CONFIRM:
					getResult().setNextAction(StepAction.NEXT_STEP);
					commandStatus = StepCommandStatus.SKIP_STEP;
					break;
				default:
					break;
			}
		}
		return commandStatus;
	}

	private void executeStep() {
		useMage();
		int[] roll = getGameState().getDiceRoller().rollWeather();
		int sum = roll[0] + roll[1];
		Weather weather = DiceInterpreter.getInstance().interpretWeather(sum);
		Map<String, Integer> weatherOptions = new HashMap<>();
		weatherOptions.put(weather.name(), 0);

		for (int i = 1; i < 3; i++) {
			addWeather(weatherOptions, sum, i);
			addWeather(weatherOptions, sum, i * -1);
		}

		getResult().addReport(new ReportWeatherMageRoll(roll));

		if (weatherOptions.size() > 1) {
			UtilServerDialog.showDialog(getGameState(), new DialogSelectWeatherParameter(weatherOptions), false);
		} else {
			Optional<String> newWeatherString = weatherOptions.keySet().stream().findFirst();
			if (newWeatherString.isPresent()) {
				Weather newWeather = Weather.valueOf(newWeatherString.get());

				Weather oldWeather = getGameState().getGame().getFieldModel().getWeather();

				if (newWeather != oldWeather) {
					replaceWeather(newWeather, weatherOptions.get(newWeatherString.get()), ReportWeatherMageResult.Effect.NO_CHOICE);
					UtilServerDialog.showDialog(getGameState(), new DialogInformationOkayParameter(DIALOG_TITLE,
						"Weather changed to " + newWeather.getName() + ".", true), false);
				} else {
					getResult().addReport(new ReportWeatherMageResult(0, newWeather, ReportWeatherMageResult.Effect.NO_CHANGE, newWeather));
					UtilServerDialog.showDialog(getGameState(), new DialogInformationOkayParameter(DIALOG_TITLE,
						"The weather did not change.", true), false);
				}

			} else {
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		}

	}

	private void addWeather(Map<String, Integer> weatherOptions, int sum, int modifier) {
		String weatherName = DiceInterpreter.getInstance().interpretWeather(Math.min(12, Math.max(2, sum + modifier))).name();
		if (!weatherOptions.containsKey(weatherName)) {
			weatherOptions.put(weatherName, modifier);
		}
	}

	private void replaceWeather(Weather weather, int modifier, ReportWeatherMageResult.Effect effect) {
		Weather oldWeather = getGameState().replaceWeather(weather);
		getResult().addReport(new ReportWeatherMageResult(modifier, weather, effect, oldWeather));
	}

	private void useMage() {
		Game game = getGameState().getGame();
		InducementSet inducementSet = game.isHomePlaying() ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
		inducementSet.getInducementTypes().stream().filter(type -> type.hasUsage(Usage.CHANGE_WEATHER)).findFirst().ifPresent(
			type -> UtilServerInducementUse.useInducement(type, 1, inducementSet)
		);
	}
}
