package com.balancedbytes.games.ffb.net.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;




/**
 * 
 * @author Kalimar
 */
public class ClientCommandUserSettings extends NetCommand {
  
  private Map<String, String> fSettings;
  
  public ClientCommandUserSettings() {
    fSettings = new HashMap<String, String>();
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
    String[] names = fSettings.keySet().toArray(new String[fSettings.size()]);
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

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    String[] settingNames = getSettingNames();
    String[] settingValues = new String[settingNames.length];
    for (int i = 0; i < settingNames.length; i++) {
      settingValues[i] = getSettingValue(settingNames[i]);
    }
    pByteList.addStringArray(settingNames);
    pByteList.addStringArray(settingValues);
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    String[] settingNames = pByteArray.getStringArray();
    String[] settingValues = pByteArray.getStringArray();
    init(settingNames, settingValues);
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    String[] settingNames = getSettingNames();
    IJsonOption.SETTING_NAMES.addTo(jsonObject, settingNames);
    String[] settingValues = new String[settingNames.length];
    for (int i = 0; i < settingNames.length; i++) {
      settingValues[i] = getSettingValue(settingNames[i]);
    }
    IJsonOption.SETTING_VALUES.addTo(jsonObject, settingValues);
    return jsonObject;
  }

  public ClientCommandUserSettings initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    init(
        IJsonOption.SETTING_NAMES.getFrom(jsonObject),
        IJsonOption.SETTING_VALUES.getFrom(jsonObject)
    );
    return this;
  }
  
}
