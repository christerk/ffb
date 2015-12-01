package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.Pushback;
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
