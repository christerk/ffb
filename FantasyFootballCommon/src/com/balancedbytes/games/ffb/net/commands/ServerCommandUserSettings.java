package com.balancedbytes.games.ffb.net.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandUserSettings extends ServerCommand {

	private Map<String, String> fUserSettings;

	public ServerCommandUserSettings() {
		fUserSettings = new HashMap<String, String>();
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

	public ServerCommandUserSettings initFrom(JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
		String[] userSettingNames = IJsonOption.USER_SETTING_NAMES.getFrom(jsonObject);
		String[] userSettingValues = IJsonOption.USER_SETTING_VALUES.getFrom(jsonObject);
		init(userSettingNames, userSettingValues);
		return this;
	}

}
