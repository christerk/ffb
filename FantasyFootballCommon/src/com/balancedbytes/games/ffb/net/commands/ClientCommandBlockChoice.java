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
public class ClientCommandBlockChoice extends NetCommand {
  
  private int fDiceIndex;
  
  public ClientCommandBlockChoice() {
    super();
  }

  public ClientCommandBlockChoice(int pDiceIndex) {
    fDiceIndex = pDiceIndex;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_BLOCK_CHOICE;
  }
  
  public int getDiceIndex() {
    return fDiceIndex;
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getDiceIndex());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fDiceIndex = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.DICE_INDEX.addTo(jsonObject, fDiceIndex);
    return jsonObject;
  }
  
  public ClientCommandBlockChoice initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fDiceIndex = IJsonOption.DICE_INDEX.getFrom(jsonObject);
    return this;
  }
    
}
