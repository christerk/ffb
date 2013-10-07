package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.ClientModeFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class ServerCommandLeave extends ServerCommand {

  private static final String _XML_ATTRIBUTE_COACH = "coach";
  private static final String _XML_ATTRIBUTE_MODE = "mode";
  private static final String _XML_ATTRIBUTE_SPECTATORS = "spectators";
  
  private String fCoach;
  private ClientMode fClientMode;
  private int fSpectators;
  
  public ServerCommandLeave() {
    super();
  }
  
  public ServerCommandLeave(String pCoach, ClientMode pClientMode, int pSpectators) {
    fCoach = pCoach;
    fClientMode = pClientMode;
    fSpectators = pSpectators;
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_LEAVE;
  }

  public String getCoach() {
    return fCoach;
  }
  
  public ClientMode getClientMode() {
    return fClientMode;
  }
  
  public int getSpectators() {
    return fSpectators;
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
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COACH, getCoach());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MODE, (getClientMode() != null) ? getClientMode().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SPECTATORS, getSpectators());
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
    pByteList.addString(getCoach());
    pByteList.addByte((byte) getClientMode().getId());
    pByteList.addSmallInt(getSpectators());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    fCoach = pByteArray.getString();
    fClientMode = new ClientModeFactory().forId(pByteArray.getByte());
    fSpectators = pByteArray.getSmallInt();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    IJsonOption.COACH.addTo(jsonObject, fCoach);
    IJsonOption.CLIENT_MODE.addTo(jsonObject, fClientMode);
    IJsonOption.SPECTATORS.addTo(jsonObject, fSpectators);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
    fCoach = IJsonOption.COACH.getFrom(jsonObject);
    fClientMode = (ClientMode) IJsonOption.CLIENT_MODE.getFrom(jsonObject);
    fSpectators = IJsonOption.SPECTATORS.getFrom(jsonObject);
  }
  
}
