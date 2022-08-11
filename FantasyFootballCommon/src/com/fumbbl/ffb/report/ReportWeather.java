package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportWeather extends NoDiceReport {

	private Weather fWeather;
	private int[] fWeatherRoll;

	public ReportWeather() {
		super();
	}

	public ReportWeather(Weather pWeather, int[] pRoll) {
		fWeather = pWeather;
		fWeatherRoll = pRoll;
	}

	public ReportId getId() {
		return ReportId.WEATHER;
	}

	public Weather getWeather() {
		return fWeather;
	}

	public int[] getWeatherRoll() {
		return fWeatherRoll;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportWeather(getWeather(), getWeatherRoll());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.WEATHER.addTo(jsonObject, fWeather);
		IJsonOption.WEATHER_ROLL.addTo(jsonObject, fWeatherRoll);
		return jsonObject;
	}

	public ReportWeather initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fWeather = (Weather) IJsonOption.WEATHER.getFrom(source, jsonObject);
		fWeatherRoll = IJsonOption.WEATHER_ROLL.getFrom(source, jsonObject);
		return this;
	}

}
