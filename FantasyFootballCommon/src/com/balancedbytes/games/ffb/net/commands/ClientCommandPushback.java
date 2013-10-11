package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.Pushback;
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
public class ClientCommandPushback extends NetCommand {
  
  private Pushback fPushback;
  
  public ClientCommandPushback() {
    super();
  }

  public ClientCommandPushback(Pushback pPushback) {
    if (pPushback == null) {
      throw new IllegalArgumentException("Parameter pushback must not be null.");
    }
    fPushback = pPushback;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_PUSHBACK;
  }
  
  public Pushback getPushback() {
    return fPushback;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    getPushback().addTo(pByteList);    
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPushback = new Pushback();
    fPushback.initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.PUSHBACK.addTo(jsonObject, fPushback.toJsonValue());
    return jsonObject;
  }
  
  public ClientCommandPushback initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fPushback = new Pushback();
    fPushback.initFrom(IJsonOption.PUSHBACK.getFrom(jsonObject));
    return this;
  }

}
