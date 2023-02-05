package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportWeatherMageResult;

@ReportMessageType(ReportId.WEATHER_MAGE_RESULT)
@RulesCollection(Rules.BB2020)
public class WeatherMageResultMessage extends ReportMessageBase<ReportWeatherMageResult> {

	@Override
	protected void render(ReportWeatherMageResult report) {
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());

		ReportWeatherMageResult.Effect effect = report.getEffect();

		switch (effect) {
			case NO_CHANGE:
				println(getIndent() + 1, TextStyle.NONE, "The mage fails to influence the weather enough to cause any changes");
				break;
			case CHANGED:
				reportChangedWeather(report, mechanic);
				break;
			default:
				println(getIndent() + 1, TextStyle.EXPLANATION, "There was only one option");
				reportChangedWeather(report, mechanic);
		}
	}

	private void reportChangedWeather(ReportWeatherMageResult report, GameMechanic mechanic) {
		println(getIndent() + 1, TextStyle.NONE, "The weather is changed to " + report.getNewWeather().getName()
			+ ". It will return to " + report.getOldWeather().getName() + " at the end of your "
			+ (game.isHomePlaying() ? "opponent's " : "") + "turn or the end of the drive");
		println(getIndent() + 1, TextStyle.EXPLANATION, mechanic.weatherDescription(report.getNewWeather()));

	}
}
