package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportWeatherMageRoll extends NoDiceReport {

	private int[] fWeatherRoll;

	public ReportWeatherMageRoll() {
		super();
	}

	public ReportWeatherMageRoll(int[] pRoll) {
		fWeatherRoll = pRoll;
	}

	public ReportId getId() {
		return ReportId.WEATHER_MAGE_ROLL;
	}


	public int[] getWeatherRoll() {
		return fWeatherRoll;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportWeatherMageRoll(getWeatherRoll());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.WEATHER_ROLL.addTo(jsonObject, fWeatherRoll);
		return jsonObject;
	}

	public ReportWeatherMageRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fWeatherRoll = IJsonOption.WEATHER_ROLL.getFrom(source, jsonObject);
		return this;
	}

}
