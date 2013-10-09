package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandFoul extends NetCommand implements ICommandWithActingPlayer {

  private static final String _XML_ATTRIBUTE_ACTING_PLAYER_ID = "actingPlayerId";
  private static final String _XML_ATTRIBUTE_DEFENDER_ID = "defenderId";

  private String fActingPlayerId;
  private String fDefenderId;

  public ClientCommandFoul() {
    super();
  }

  public ClientCommandFoul(String pActingPlayerId, String pDefenderId) {
    fActingPlayerId = pActingPlayerId;
    fDefenderId = pDefenderId;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_FOUL;
  }

  public String getActingPlayerId() {
    return fActingPlayerId;
  }

  public String getDefenderId() {
    return fDefenderId;
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ACTING_PLAYER_ID, getActingPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DEFENDER_ID, getDefenderId());
    UtilXml.addEmptyElement(pHandler, getId().getName(), attributes);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 2;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getActingPlayerId());
    pByteList.addString(getDefenderId());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    if (byteArraySerializationVersion > 1) {
      fActingPlayerId = pByteArray.getString();
    }
    fDefenderId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
    IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
    return jsonObject;
  }
  
  public ClientCommandFoul initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(jsonObject);
    fDefenderId = IJsonOption.DEFENDER_ID.getFrom(jsonObject);
    return this;
  }

}
