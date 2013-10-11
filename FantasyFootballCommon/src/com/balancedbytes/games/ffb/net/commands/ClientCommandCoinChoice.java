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
public class ClientCommandCoinChoice extends NetCommand {
  
  private boolean fChoiceHeads;
  
  public ClientCommandCoinChoice() {
    super();
  }

  public ClientCommandCoinChoice(boolean pChoiceHeads) {
    fChoiceHeads = pChoiceHeads;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_COIN_CHOICE;
  }
  
  public boolean isChoiceHeads() {
    return fChoiceHeads;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addBoolean(isChoiceHeads());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChoiceHeads = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.CHOICE_HEADS.addTo(jsonObject, fChoiceHeads);
    return jsonObject;
  }
  
  public ClientCommandCoinChoice initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fChoiceHeads = IJsonOption.CHOICE_HEADS.getFrom(jsonObject);
    return this;
  }

}
