package com.fumbbl.ffb.net.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;

/**
 *
 * @author Kalimar
 */
public class ServerCommandUserSettings extends ServerCommand {

	private Map<String, String> fUserSettings;

	public ServerCommandUserSettings() {
		fUserSettings = new HashMap<>();
	}

	public ServerCommandUserSettings(String[] pUserSettingNames, String[] pUserSettingValues) {
		this();
		init(pUserSettingNames, pUserSettingValues);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_USER_SETTINGS;
	}

	public void addUserSetting(String pName, String pValue) {
		fUserSettings.put(pName, pValue);
	}

	public String[] getUserSettingNames() {
		String[] names = fUserSettings.keySet().toArray(new String[fUserSettings.size()]);
		Arrays.sort(names);
		return names;
	}

	public String getUserSettingValue(String pName) {
		return fUserSettings.get(pName);
	}

	private void init(String[] pSettingNames, String[] pSettingValues) {
		fUserSettings.clear();
		if (ArrayTool.isProvided(pSettingNames) && ArrayTool.isProvided(pSettingValues)) {
			for (int i = 0; i < pSettingNames.length; i++) {
				addUserSetting(pSettingNames[i], pSettingValues[i]);
			}
		}
	}

	public boolean isReplayable() {
		return false;
	}

	@Override
	public FactoryContext getContext() {
		return FactoryContext.APPLICATION;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		String[] userSettingNames = getUserSettingNames();
		String[] userSettingValues = new String[userSettingNames.length];
		for (int i = 0; i < userSettingNames.length; i++) {
			userSettingValues[i] = getUserSettingValue(userSettingNames[i]);
		}
		IJsonOption.USER_SETTING_NAMES.addTo(jsonObject, userSettingNames);
		IJsonOption.USER_SETTING_VALUES.addTo(jsonObject, userSettingValues);
		return jsonObject;
	}

	public ServerCommandUserSettings initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(game, jsonObject));
		String[] userSettingNames = IJsonOption.USER_SETTING_NAMES.getFrom(game, jsonObject);
		String[] userSettingValues = IJsonOption.USER_SETTING_VALUES.getFrom(game, jsonObject);
		init(userSettingNames, userSettingValues);
		return this;
	}

}
