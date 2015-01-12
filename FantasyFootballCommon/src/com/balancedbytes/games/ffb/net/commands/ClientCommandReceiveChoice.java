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
public class ClientCommandReceiveChoice extends NetCommand {

  private boolean fChoiceReceive;

  public ClientCommandReceiveChoice() {
    super();
  }

  public ClientCommandReceiveChoice(boolean pChoiceReceive) {
    fChoiceReceive = pChoiceReceive;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_RECEIVE_CHOICE;
  }

  public boolean isChoiceReceive() {
    return fChoiceReceive;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.CHOICE_RECEIVE.addTo(jsonObject, fChoiceReceive);
    return jsonObject;
  }

  public ClientCommandReceiveChoice initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fChoiceReceive = IJsonOption.CHOICE_RECEIVE.getFrom(jsonObject);
    return this;
  }

}
