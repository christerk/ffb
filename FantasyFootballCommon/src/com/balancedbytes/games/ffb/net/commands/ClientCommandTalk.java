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
public class ClientCommandTalk extends NetCommand {

  private String fTalk;

  public ClientCommandTalk() {
    super();
  }

  public ClientCommandTalk(String pTalk) {
    fTalk = pTalk;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_TALK;
  }

  public String getTalk() {
    return fTalk;
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getTalk());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTalk = pByteArray.getString();
    return byteArraySerializationVersion;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.TALK.addTo(jsonObject, fTalk);
    return jsonObject;
  }

  public ClientCommandTalk initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fTalk = IJsonOption.TALK.getFrom(jsonObject);
    return this;
  }

}
