package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ServerCommandTeamSetupList extends ServerCommand {

  private static final String _XML_ATTRIBUTE_NAME = "name";
  private static final String _XML_TAG_SETUP = "setup";
  
  private List<String> fSetupNames;
  
  public ServerCommandTeamSetupList() {
    fSetupNames = new ArrayList<String>();
  }
  
  public ServerCommandTeamSetupList(String[] pSetupNames) {
    this();
    addSetupNames(pSetupNames);
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_TEAM_SETUP_LIST;
  }
  
  public String[] getSetupNames() {
    return fSetupNames.toArray(new String[fSetupNames.size()]);
  }
  
  private void addSetupName(String pSetupName) {
    if (StringTool.isProvided(pSetupName)) {
      fSetupNames.add(pSetupName);
    }
  }
  
  private void addSetupNames(String[] pSetupNames) {
    if (ArrayTool.isProvided(pSetupNames)) {
      for (String setupName : pSetupNames) {
        addSetupName(setupName);
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
    String[] setupNames = getSetupNames();
    if (ArrayTool.isProvided(setupNames)) {
      for (String setupName : setupNames) {
        attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, setupName);
        UtilXml.addEmptyElement(pHandler, _XML_TAG_SETUP, attributes);
      }
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
    pByteList.addStringArray(getSetupNames());
  }
    
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    addSetupNames(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    IJsonOption.SETUP_NAMES.addTo(jsonObject, fSetupNames);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
    addSetupNames(IJsonOption.SETUP_NAMES.getFrom(jsonObject));
  }
    
}
