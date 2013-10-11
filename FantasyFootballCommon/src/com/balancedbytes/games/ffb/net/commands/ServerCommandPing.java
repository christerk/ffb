package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;




/**
 * 
 * @author Kalimar
 */
public class ServerCommandPing extends ServerCommand {

  private long fTimestamp;
  private transient long fReceived;
  
  public ServerCommandPing() {
    super();
  }

  public ServerCommandPing(long pTimestamp) {
    fTimestamp = pTimestamp;
  }
 
  public NetCommandId getId() {
    return NetCommandId.SERVER_PING;
  }
  
  public long getTimestamp() {
    return fTimestamp;
  }
  
  public void setReceived(long pReceived) {
    fReceived = pReceived;
  }
  
  public long getReceived() {
    return fReceived;
  }
  
  public boolean isReplayable() {
    return false;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt(getCommandNr());
    pByteList.addLong(getTimestamp());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    fTimestamp = pByteArray.getLong();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    IJsonOption.TIMESTAMP.addTo(jsonObject, fTimestamp);
    return jsonObject;
  }

  public ServerCommandPing initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
    fTimestamp = IJsonOption.TIMESTAMP.getFrom(jsonObject);
    return this;
  }
    
}
