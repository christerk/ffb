package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.net.ServerStatusFactory;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ServerCommandStatus extends ServerCommand {
  
  private static final String _XML_ATTRIBUTE_STATUS = "status";
  private static final String _XML_ATTRIBUTE_MESSAGE = "message";
  
  private ServerStatus fServerStatus;
  private String fMessage;
  
  public ServerCommandStatus() {
    super();
  }
  
  public ServerCommandStatus(ServerStatus pServerStatus, String pMessage) {
    fServerStatus = pServerStatus;
    fMessage = pMessage;
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_STATUS;
  }

  public ServerStatus getServerStatus() {
    return fServerStatus;
  }
  
  public String getMessage() {
    return fMessage;
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
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STATUS, (getServerStatus() != null) ? getServerStatus().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MESSAGE, getMessage());
    UtilXml.addEmptyElement(pHandler, getId().getName(), attributes);
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
    if (getServerStatus() != null) {
      pByteList.addByte((byte) getServerStatus().getId());
    } else {
      pByteList.addByte((byte) 0);
    }
    pByteList.addString(getMessage());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    fServerStatus = new ServerStatusFactory().forId(pByteArray.getByte());
    fMessage = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    IJsonOption.SERVER_STATUS.addTo(jsonObject, fServerStatus);
    IJsonOption.MESSAGE.addTo(jsonObject, fMessage);
    return jsonObject;
  }

  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
    fServerStatus = (ServerStatus) IJsonOption.SERVER_STATUS.getFrom(jsonObject);
    fMessage = IJsonOption.MESSAGE.getFrom(jsonObject);
  }

 }
