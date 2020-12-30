package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportWeather implements IReport {

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

	public ReportWeather initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fWeather = (Weather) IJsonOption.WEATHER.getFrom(game, jsonObject);
		fWeatherRoll = IJsonOption.WEATHER_ROLL.getFrom(game, jsonObject);
		return this;
	}

}
