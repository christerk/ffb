package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Kalimar
 */
public class ClientCommandUserSettings extends ClientCommand {

	private final Map<CommonProperty, String> fSettings;

	public ClientCommandUserSettings() {
		fSettings = new HashMap<>();
	}

	public ClientCommandUserSettings(CommonProperty[] pSettingNames, String[] pSettingValues) {
		this();
		init(pSettingNames, pSettingValues);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USER_SETTINGS;
	}

	public void addSetting(CommonProperty pName, String pValue) {
		fSettings.put(pName, pValue);
	}

	public CommonProperty[] getSettingNames() {
		CommonProperty[] names = fSettings.keySet().stream().filter(Objects::nonNull).toArray(CommonProperty[]::new);
		Arrays.sort(names);
		return names;
	}

	public String getSettingValue(CommonProperty pName) {
		return fSettings.get(pName);
	}

	private void init(CommonProperty[] pSettingNames, String[] pSettingValues) {
		if (ArrayTool.isProvided(pSettingNames) && ArrayTool.isProvided(pSettingValues)) {
			for (int i = 0; i < pSettingNames.length; i++) {
				addSetting(pSettingNames[i], pSettingValues[i]);
			}
		}
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		CommonProperty[] settingNames = getSettingNames();
		IJsonOption.SETTING_NAMES.addTo(jsonObject, Arrays.stream(settingNames).map(CommonProperty::getKey).toArray(String[]::new));
		String[] settingValues = new String[settingNames.length];
		for (int i = 0; i < settingNames.length; i++) {
			settingValues[i] = getSettingValue(settingNames[i]);
		}
		IJsonOption.SETTING_VALUES.addTo(jsonObject, settingValues);
		return jsonObject;
	}

	public ClientCommandUserSettings initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		init(Arrays.stream(IJsonOption.SETTING_NAMES.getFrom(source, jsonObject)).map(CommonProperty::forKey).toArray(CommonProperty[]::new),
			IJsonOption.SETTING_VALUES.getFrom(source, jsonObject));
		return this;
	}

}
