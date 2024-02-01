package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ServerCommandUserSettings extends ServerCommand {

	private final Map<CommonProperty, String> fUserSettings;

	public ServerCommandUserSettings() {
		fUserSettings = new HashMap<>();
	}

	public ServerCommandUserSettings(CommonProperty[] pUserSettingNames, String[] pUserSettingValues) {
		this();
		init(pUserSettingNames, pUserSettingValues);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_USER_SETTINGS;
	}

	public void addUserSetting(CommonProperty pName, String pValue) {
		fUserSettings.put(pName, pValue);
	}

	public CommonProperty[] getUserSettingNames() {
		CommonProperty[] names = fUserSettings.keySet().toArray(new CommonProperty[0]);
		Arrays.sort(names);
		return names;
	}

	public String getUserSettingValue(CommonProperty pName) {
		return fUserSettings.get(pName);
	}

	private void init(CommonProperty[] pSettingNames, String[] pSettingValues) {
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
		CommonProperty[] userSettingNames = getUserSettingNames();
		String[] userSettingValues = new String[userSettingNames.length];
		for (int i = 0; i < userSettingNames.length; i++) {
			userSettingValues[i] = getUserSettingValue(userSettingNames[i]);
		}
		IJsonOption.USER_SETTING_NAMES.addTo(jsonObject, Arrays.stream(userSettingNames).map(CommonProperty::getKey).toArray(String[]::new));
		IJsonOption.USER_SETTING_VALUES.addTo(jsonObject, userSettingValues);
		return jsonObject;
	}

	public ServerCommandUserSettings initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		String[] userSettingNames = IJsonOption.USER_SETTING_NAMES.getFrom(source, jsonObject);
		String[] userSettingValues = IJsonOption.USER_SETTING_VALUES.getFrom(source, jsonObject);
		init(Arrays.stream(userSettingNames).map(CommonProperty::forKey).toArray(CommonProperty[]::new), userSettingValues);
		return this;
	}

}
