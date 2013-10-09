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
public class ClientCommandBlock extends NetCommand implements ICommandWithActingPlayer {

  private static final String _XML_ATTRIBUTE_ACTING_PLAYER_ID = "actingPlayerId";
  private static final String _XML_ATTRIBUTE_DEFENDER_ID = "defenderId";
  private static final String _XML_ATTRIBUTE_USING_STAB = "usingStab";

  private String fActingPlayerId;
  private String fDefenderId;
  private boolean fUsingStab;

  public ClientCommandBlock() {
    super();
  }

  public ClientCommandBlock(String pActingPlayerId, String pDefenderId, boolean pUsingStab) {
    fActingPlayerId = pActingPlayerId;
    fDefenderId = pDefenderId;
    fUsingStab = pUsingStab;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_BLOCK;
  }

  public String getActingPlayerId() {
    return fActingPlayerId;
  }

  public String getDefenderId() {
    return fDefenderId;
  }

  public boolean isUsingStab() {
    return fUsingStab;
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ACTING_PLAYER_ID, getActingPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DEFENDER_ID, getDefenderId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_USING_STAB, isUsingStab());
    UtilXml.addEmptyElement(pHandler, getId().getName(), attributes);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 3;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getActingPlayerId());
    pByteList.addString(getDefenderId());
    pByteList.addBoolean(isUsingStab());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    if (byteArraySerializationVersion > 2) {
      fActingPlayerId = pByteArray.getString();
    }
    fDefenderId = pByteArray.getString();
    fUsingStab = pByteArray.getBoolean();
    if (byteArraySerializationVersion < 2) {
      pByteArray.getByte(); // reRollSource obsolete since version 2
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
    IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
    IJsonOption.USING_STAB.addTo(jsonObject, fUsingStab);
    return jsonObject;
  }
  
  public ClientCommandBlock initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(jsonObject);
    fDefenderId = IJsonOption.DEFENDER_ID.getFrom(jsonObject);
    fUsingStab = IJsonOption.USING_STAB.getFrom(jsonObject);
    return this;
  }

}
