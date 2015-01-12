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
public class ClientCommandTeamSetupDelete extends NetCommand {

  private String fSetupName;

  public ClientCommandTeamSetupDelete() {
    super();
  }

  public ClientCommandTeamSetupDelete(String pSetupName) {
    fSetupName = pSetupName;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_TEAM_SETUP_DELETE;
  }

  public String getSetupName() {
    return fSetupName;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.SETUP_NAME.addTo(jsonObject, fSetupName);
    return jsonObject;
  }

  public ClientCommandTeamSetupDelete initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fSetupName = IJsonOption.SETUP_NAME.getFrom(jsonObject);
    return this;
  }

}
