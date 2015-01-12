package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
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
public class ClientCommandUseReRoll extends NetCommand {

  private ReRolledAction fReRolledAction;
  private ReRollSource fReRollSource;

  public ClientCommandUseReRoll() {
    super();
  }

  public ClientCommandUseReRoll(ReRolledAction pReRolledAction, ReRollSource pReRollSource) {
    fReRolledAction = pReRolledAction;
    fReRollSource = pReRollSource;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_USE_RE_ROLL;
  }

  public ReRolledAction getReRolledAction() {
    return fReRolledAction;
  }

  public ReRollSource getReRollSource() {
    return fReRollSource;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, fReRolledAction);
    IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, fReRollSource);
    return jsonObject;
  }

  public ClientCommandUseReRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fReRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(jsonObject);
    fReRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(jsonObject);
    return this;
  }

}
