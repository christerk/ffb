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
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class ClientCommandSetMarker extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_TEXT = "text";
  
  private static final String _XML_TAG_PLAYER_ID = "playerId";

  private static final String _XML_TAG_COORDINATE = "coordinate";
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  
  private String fPlayerId;
  private FieldCoordinate fCoordinate;
  private String fText;
  
  public ClientCommandSetMarker() {
    super();
  }

  public ClientCommandSetMarker(FieldCoordinate pCoordinate, String pText) {
    fCoordinate = pCoordinate;
    fText = pText;
  }
  
  public ClientCommandSetMarker(String pPlayerId, String pText) {
    fPlayerId = pPlayerId;
    fText = pText;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_SET_MARKER;
  }
  
  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public String getText() {
    return fText;
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEXT, getText());
    UtilXml.startElement(pHandler, getId().getName());
    if (getCoordinate() != null) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getCoordinate().getX());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getCoordinate().getY());
      UtilXml.addEmptyElement(pHandler, _XML_TAG_COORDINATE, attributes);
    }
    if (StringTool.isProvided(getPlayerId())) {
      UtilXml.addValueElement(pHandler, _XML_TAG_PLAYER_ID, getPlayerId());
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
    pByteList.addFieldCoordinate(getCoordinate());
    pByteList.addString(getPlayerId());
    pByteList.addString(getText());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fCoordinate = pByteArray.getFieldCoordinate();
    fPlayerId = pByteArray.getString();
    fText = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.TEXT.addTo(jsonObject, fText);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fText = IJsonOption.TEXT.getFrom(jsonObject);
  }
    
}
