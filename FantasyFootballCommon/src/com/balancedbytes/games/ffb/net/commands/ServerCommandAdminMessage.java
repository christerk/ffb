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
public class ServerCommandAdminMessage extends ServerCommand {
  
  private static final String _XML_TAG_MESSAGE = "message";
  
  private List<String> fMessages;
  
  public ServerCommandAdminMessage() {
    fMessages = new ArrayList<String>();
  }
  
  public ServerCommandAdminMessage(String[] pMessages) {
    this();
    addMessages(pMessages);
  }
  
  private void addMessage(String pMessage) {
    if (StringTool.isProvided(pMessage)) {
      fMessages.add(pMessage);
    }
  }
  
  private void addMessages(String[] pMessages) {
    if (ArrayTool.isProvided(pMessages)) {
      for (String message : pMessages) {
        addMessage(message);
      }
    }
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_ADMIN_MESSAGE;
  }
  
  public String[] getMessages() {
    return fMessages.toArray(new String[fMessages.size()]);
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
    String[] messages = getMessages();
    if (ArrayTool.isProvided(messages)) {
      for (String message: messages) {
        UtilXml.addValueElement(pHandler, _XML_TAG_MESSAGE, message);
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
    pByteList.addStringArray(getMessages());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    addMessages(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    IJsonOption.MESSAGES.addTo(jsonObject, fMessages);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
    addMessages(IJsonOption.MESSAGES.getFrom(jsonObject));
  }

 }
