package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.ClientStateId;
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
