package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;


public class ClientCommandSelectWeather extends ClientCommand {

	private int modifier;
	private String weatherName;

	public ClientCommandSelectWeather() {
		super();
	}

	public ClientCommandSelectWeather(int modifier, String weatherName) {
		this.modifier = modifier;
		this.weatherName = weatherName;
	}

	public int getModifier() {
		return modifier;
	}

	public String getWeatherName() {
		return weatherName;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_SELECT_WEATHER;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.MODIFIER.addTo(jsonObject, modifier);
		IJsonOption.NAME.addTo(jsonObject, weatherName);
		return jsonObject;
	}

	public ClientCommandSelectWeather initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		modifier = IJsonOption.MODIFIER.getFrom(source, jsonObject);
		weatherName = IJsonOption.NAME.getFrom(source, jsonObject);
		return this;
	}

}
