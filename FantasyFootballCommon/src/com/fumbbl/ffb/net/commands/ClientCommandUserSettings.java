package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class ClientCommandUserSettings extends ClientCommand {

	private final Map<String, String> fSettings;

	public ClientCommandUserSettings() {
		fSettings = new HashMap<>();
	}

	public ClientCommandUserSettings(String[] pSettingNames, String[] pSettingValues) {
		this();
		init(pSettingNames, pSettingValues);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USER_SETTINGS;
	}

	public void addSetting(String pName, String pValue) {
		fSettings.put(pName, pValue);
	}

	public String[] getSettingNames() {
		String[] names = fSettings.keySet().toArray(new String[0]);
		Arrays.sort(names);
		return names;
	}

	public String getSettingValue(String pName) {
		return fSettings.get(pName);
	}

	private void init(String[] pSettingNames, String[] pSettingValues) {
		if (ArrayTool.isProvided(pSettingNames) && ArrayTool.isProvided(pSettingValues)) {
			for (int i = 0; i < pSettingNames.length; i++) {
				addSetting(pSettingNames[i], pSettingValues[i]);
			}
		}
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		String[] settingNames = getSettingNames();
		IJsonOption.SETTING_NAMES.addTo(jsonObject, settingNames);
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
		init(IJsonOption.SETTING_NAMES.getFrom(source, jsonObject), IJsonOption.SETTING_VALUES.getFrom(source, jsonObject));
		return this;
	}

}
