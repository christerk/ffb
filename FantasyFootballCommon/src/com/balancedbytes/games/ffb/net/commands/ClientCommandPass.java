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
public class ClientCommandPass extends NetCommand implements ICommandWithActingPlayer {
  
  private String fActingPlayerId;
  private FieldCoordinate fTargetCoordinate;
  
  public ClientCommandPass() {
    super();
  }

  public ClientCommandPass(String pActingPlayerId, FieldCoordinate pTargetCoordinate) {
  	fActingPlayerId = pActingPlayerId;
    fTargetCoordinate = pTargetCoordinate;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_PASS;
  }
  
  public String getActingPlayerId() {
    return fActingPlayerId;
  }

  public FieldCoordinate getTargetCoordinate() {
    return fTargetCoordinate;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 2;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getActingPlayerId());
    pByteList.addFieldCoordinate(getTargetCoordinate());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    if (byteArraySerializationVersion > 1) {
    	fActingPlayerId = pByteArray.getString();
    }
    fTargetCoordinate = pByteArray.getFieldCoordinate();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
    IJsonOption.TARGET_COORDINATE.addTo(jsonObject, fTargetCoordinate);
    return jsonObject;
  }
  
  public ClientCommandPass initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(jsonObject);
    fTargetCoordinate = IJsonOption.TARGET_COORDINATE.getFrom(jsonObject);
    return this;
  }

}
