package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportWeatherMageResult extends NoDiceReport {

	private int modifier;
	private Weather newWeather, oldWeather;
	private Effect effect;

	public ReportWeatherMageResult() {
		super();
	}

	public ReportWeatherMageResult(int modifier, Weather newWeather, Effect effect, Weather oldWeather) {
		this.modifier = modifier;
		this.newWeather = newWeather;
		this.oldWeather = oldWeather;
		this.effect = effect;
	}

	public ReportId getId() {
		return ReportId.WEATHER_MAGE_RESULT;
	}

	public int getModifier() {
		return modifier;
	}

	public Weather getNewWeather() {
		return newWeather;
	}

	public Weather getOldWeather() {
		return oldWeather;
	}

	public Effect getEffect() {
		return effect;
	}


	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportWeatherMageResult(modifier, newWeather, effect, oldWeather);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.MODIFIER.addTo(jsonObject, modifier);
		IJsonOption.WEATHER.addTo(jsonObject, newWeather);
		IJsonOption.OLD_WEATHER.addTo(jsonObject, oldWeather);
		IJsonOption.EFFECT.addTo(jsonObject, effect.name());
		return jsonObject;
	}

	public ReportWeatherMageResult initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		modifier = IJsonOption.MODIFIER.getFrom(source, jsonObject);
		newWeather = (Weather) IJsonOption.WEATHER.getFrom(source, jsonObject);
		oldWeather = (Weather) IJsonOption.OLD_WEATHER.getFrom(source, jsonObject);
		effect = Effect.valueOf(IJsonOption.EFFECT.getFrom(source, jsonObject));
		return this;
	}

	public enum Effect {
		CHANGED, NO_CHANGE, NO_CHOICE
	}

}
