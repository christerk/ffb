package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.UtilNetCommand;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public abstract class InternalServerCommand extends NetCommand {
  
  protected static final String XML_ATTRIBUTE_GAME_ID = "gameId";
  
  private long fGameId;

  public InternalServerCommand(long pGameId) {
    setGameId(pGameId);
  }
  
  public long getGameId() {
    return fGameId;
  }
  
  protected void setGameId(long pGameId) {
    fGameId = pGameId;
  }
  
  public boolean isInternal() {
    return true;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addLong(fGameId);
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fGameId = pByteArray.getLong();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.GAME_ID.addTo(jsonObject, fGameId);
    return jsonObject;
  }

  public InternalServerCommand initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fGameId = IJsonOption.GAME_ID.getFrom(jsonObject);
    return this;
  }
  
}
