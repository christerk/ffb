package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportWeatherMageRoll;

@ReportMessageType(ReportId.WEATHER_MAGE_ROLL)
@RulesCollection(Rules.BB2020)
public class WeatherMageRollMessage extends ReportMessageBase<ReportWeatherMageRoll> {

	@Override
	protected void render(ReportWeatherMageRoll report) {
		int[] roll = report.getWeatherRoll();
		String status = "Weather Roll [ " + roll[0] + " ][ " + roll[1] + " ] ";
		println(getIndent(), TextStyle.ROLL, status);
		println(getIndent() + 1, TextStyle.NONE, "The weather mage works his magic");
	}
}
