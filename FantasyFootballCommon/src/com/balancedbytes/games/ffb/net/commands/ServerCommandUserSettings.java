package com.balancedbytes.games.ffb.net.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;




/**
 * 
 * @author Kalimar
 */
public class ServerCommandUserSettings extends ServerCommand {
  
  private static final String _XML_TAG_SETTING = "setting";
  private static final String _XML_ATTRIBUTE_NAME = "name";
  private static final String _XML_ATTRIBUTE_VALUE = "value";
  
  private Map<String, String> fSettings;
  
  public ServerCommandUserSettings() {
    fSettings = new HashMap<String, String>();
  }
  
  public ServerCommandUserSettings(String[] pSettingNames, String[] pSettingValues) {
    this();
    init(pSettingNames, pSettingValues);
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_USER_SETTINGS;
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
  
  public boolean isReplayable() {
    return false;
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    if (getCommandNr() > 0) {
      UtilXml.addAttribute(attributes, XML_ATTRIBUTE_COMMAND_NR, getCommandNr());
    }
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    String[] settings = getSettingNames();
    for (String setting : settings) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, setting);
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_VALUE, getSettingValue(setting));
      UtilXml.addEmptyElement(pHandler, _XML_TAG_SETTING, attributes);
    }
    UtilXml.endElement(pHandler, getId().getName());
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt(getCommandNr());
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
    setCommandNr(pByteArray.getSmallInt());
    String[] settingNames = pByteArray.getStringArray();
    String[] settingValues = pByteArray.getStringArray();
    init(settingNames, settingValues);
    return byteArraySerializationVersion;
  }
  
}
