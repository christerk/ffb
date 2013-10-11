package com.balancedbytes.games.ffb.net.commands;

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
public class ClientCommandUseApothecary extends NetCommand {

  private String fPlayerId;
  private boolean fApothecaryUsed;

  public ClientCommandUseApothecary() {
    super();
  }

  public ClientCommandUseApothecary(String pPlayerId, boolean pApothecaryUsed) {
    fPlayerId = pPlayerId;
    fApothecaryUsed = pApothecaryUsed;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_USE_APOTHECARY;
  }

  public String getPlayerId() {
    return fPlayerId;
  }

  public boolean isApothecaryUsed() {
    return fApothecaryUsed;
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addBoolean(isApothecaryUsed());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fApothecaryUsed = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.APOTHECARY_USED.addTo(jsonObject, fApothecaryUsed);
    return jsonObject;
  }

  public ClientCommandUseApothecary initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fApothecaryUsed = IJsonOption.APOTHECARY_USED.getFrom(jsonObject);
    return this;
  }

}
