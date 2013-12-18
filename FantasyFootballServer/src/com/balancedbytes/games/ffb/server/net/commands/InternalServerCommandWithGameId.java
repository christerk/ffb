package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public abstract class InternalServerCommandWithGameId extends InternalServerCommand {
  
  protected static final String XML_ATTRIBUTE_GAME_ID = "gameId";
  
  private long fGameId;

  public InternalServerCommandWithGameId(long pGameId) {
    setGameId(pGameId);
  }
  
  public long getGameId() {
    return fGameId;
  }
  
  protected void setGameId(long pGameId) {
    fGameId = pGameId;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    super.addTo(pByteList);
    pByteList.addLong(fGameId);
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = super.initFrom(pByteArray);
    fGameId = pByteArray.getLong();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.GAME_ID.addTo(jsonObject, fGameId);
    return jsonObject;
  }

  public InternalServerCommandWithGameId initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGameId = IJsonOption.GAME_ID.getFrom(jsonObject);
    return this;
  }
  
}
