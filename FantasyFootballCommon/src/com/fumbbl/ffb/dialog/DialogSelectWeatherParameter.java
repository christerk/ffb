package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

import java.util.Map;

public class DialogSelectWeatherParameter implements IDialogParameter {

	private Map<String, Integer> options;

	@Override
	public DialogId getId() {
		return DialogId.SELECT_WEATHER;
	}

	public DialogSelectWeatherParameter(Map<String, Integer> options) {
		this.options = options;
	}

	@Override
	public IDialogParameter transform() {
		return new DialogSelectWeatherParameter(options);
	}


	public Map<String, Integer> getOptions() {
		return options;
	}

	@Override
	public IDialogParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		options = IJsonOption.WEATHER_OPTIONS.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.WEATHER_OPTIONS.addTo(jsonObject, options);
		return jsonObject;
	}
}
