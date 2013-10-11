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
public class ClientCommandPettyCash extends NetCommand {

  private int fPettyCash;

  public ClientCommandPettyCash() {
    super();
  }

  public ClientCommandPettyCash(int pPettyCash) {
    fPettyCash = pPettyCash;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_PETTY_CASH;
  }

  public int getPettyCash() {
    return fPettyCash;
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addInt(getPettyCash());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPettyCash = pByteArray.getInt();
    return byteArraySerializationVersion;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.PETTY_CASH.addTo(jsonObject, fPettyCash);
    return jsonObject;
  }

  public ClientCommandPettyCash initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fPettyCash = IJsonOption.PETTY_CASH.getFrom(jsonObject);
    return this;
  }

}
