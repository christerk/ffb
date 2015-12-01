package com.balancedbytes.games.ffb.net.commands;

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
public class ClientCommandInterceptorChoice extends NetCommand {

  private String fInterceptorId;

  public ClientCommandInterceptorChoice() {
    super();
  }

  public ClientCommandInterceptorChoice(String pInterceptorId) {
    fInterceptorId = pInterceptorId;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_INTERCEPTOR_CHOICE;
  }

  public String getInterceptorId() {
    return fInterceptorId;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.INTERCEPTOR_ID.addTo(jsonObject, fInterceptorId);
    return jsonObject;
  }

  public ClientCommandInterceptorChoice initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fInterceptorId = IJsonOption.INTERCEPTOR_ID.getFrom(jsonObject);
    return this;
  }

}
