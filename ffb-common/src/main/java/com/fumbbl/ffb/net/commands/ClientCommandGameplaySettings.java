package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sends the coach's settings that influence server side decisions during a game
 * (see {@link CommonProperty#_SERVER_GAMEPLAY_SETTINGS}).
 */
public class ClientCommandGameplaySettings extends ClientCommand {

	private final Map<CommonProperty, String> settings = new LinkedHashMap<>();

	public ClientCommandGameplaySettings() {
		super();
	}

	public ClientCommandGameplaySettings(Map<CommonProperty, String> settings) {
		this();
		settings.forEach(this::addSetting);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_GAMEPLAY_SETTINGS;
	}

	public void addSetting(CommonProperty name, String value) {
		if (name != null && value != null) {
			settings.put(name, value);
		}
	}

	public Map<CommonProperty, String> getSettings() {
		return new LinkedHashMap<>(settings);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.SETTING_NAMES.addTo(jsonObject,
			settings.keySet().stream().map(CommonProperty::getKey).toArray(String[]::new));
		IJsonOption.SETTING_VALUES.addTo(jsonObject, settings.values().toArray(new String[0]));
		return jsonObject;
	}

	public ClientCommandGameplaySettings initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		String[] names = IJsonOption.SETTING_NAMES.getFrom(source, jsonObject);
		String[] values = IJsonOption.SETTING_VALUES.getFrom(source, jsonObject);
		if (names != null && values != null) {
			for (int i = 0; i < names.length && i < values.length; i++) {
				addSetting(CommonProperty.forKey(names[i]), values[i]);
			}
		}
		return this;
	}

}
