package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportWeather;

@ReportMessageType(ReportId.WEATHER)
@RulesCollection(Rules.COMMON)
public class WeatherMessage extends ReportMessageBase<ReportWeather> {

    @Override
    protected void render(ReportWeather report) {
  		int[] roll = report.getWeatherRoll();
  		StringBuilder status = new StringBuilder();
  		status.append("Weather Roll [ ").append(roll[0]).append(" ][ ").append(roll[1]).append(" ] ");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		Weather weather = report.getWeather();
  		status = new StringBuilder();
  		status.append("Weather is ").append(weather.getName());
  		println(getIndent() + 1, status.toString());
  		println(getIndent() + 1, TextStyle.EXPLANATION, weather.getDescription());
    }
}
