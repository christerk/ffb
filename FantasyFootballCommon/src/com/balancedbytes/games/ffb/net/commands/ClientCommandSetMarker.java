package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class ClientCommandSetMarker extends NetCommand {
  
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
  
  public ClientCommandSetMarker initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fText = IJsonOption.TEXT.getFrom(jsonObject);
    return this;
  }
    
}
