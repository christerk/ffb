package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.ClientStateIdFactory;
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
public class ClientCommandDebugClientState extends NetCommand {
  
  private ClientStateId fClientStateId;
  
  public ClientCommandDebugClientState() {
    super();
  }

  public ClientCommandDebugClientState(ClientStateId pClientStateId) {
    this();
    fClientStateId = pClientStateId;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_DEBUG_CLIENT_STATE;
  }
  
  public ClientStateId getClientStateId() {
    return fClientStateId;
  }
  
  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getClientStateId() != null) ? getClientStateId().getId() : 0));
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fClientStateId = new ClientStateIdFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.CLIENT_STATE_ID.addTo(jsonObject, fClientStateId);
    return jsonObject;
  }
  
  public ClientCommandDebugClientState initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fClientStateId = (ClientStateId) IJsonOption.CLIENT_STATE_ID.getFrom(jsonObject);
    return this;
  }

}
