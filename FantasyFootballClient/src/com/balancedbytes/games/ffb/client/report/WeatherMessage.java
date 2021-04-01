package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportWeather;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.WEATHER)
@RulesCollection(Rules.COMMON)
public class WeatherMessage extends ReportMessageBase<ReportWeather> {

    public WeatherMessage(StatusReport statusReport) {
        super(statusReport);
    }

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
