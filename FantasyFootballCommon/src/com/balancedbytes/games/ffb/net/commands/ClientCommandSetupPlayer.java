package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FieldCoordinate;
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
public class ClientCommandSetupPlayer extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";

  private static final String _XML_TAG_COORDINATE = "coordinate";
  
  private String fPlayerId;
  private FieldCoordinate fCoordinate;
  
  public ClientCommandSetupPlayer() {
    super();
  }

  public ClientCommandSetupPlayer(String pPlayerId, FieldCoordinate pCoordinate) {
    fPlayerId = pPlayerId;
    fCoordinate = pCoordinate;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_SETUP_PLAYER;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    if (getCoordinate() != null) {
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getCoordinate().getX());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getCoordinate().getY());
      UtilXml.addEmptyElement(pHandler, _XML_TAG_COORDINATE, attributes);
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
    pByteList.addString(getPlayerId());
    pByteList.addFieldCoordinate(getCoordinate());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fCoordinate = pByteArray.getFieldCoordinate();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
  }
      
}
