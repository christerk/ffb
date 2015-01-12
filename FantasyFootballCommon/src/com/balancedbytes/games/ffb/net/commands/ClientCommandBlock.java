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
public class ClientCommandBlock extends NetCommand implements ICommandWithActingPlayer {

  private String fActingPlayerId;
  private String fDefenderId;
  private boolean fUsingStab;

  public ClientCommandBlock() {
    super();
  }

  public ClientCommandBlock(String pActingPlayerId, String pDefenderId, boolean pUsingStab) {
    fActingPlayerId = pActingPlayerId;
    fDefenderId = pDefenderId;
    fUsingStab = pUsingStab;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_BLOCK;
  }

  public String getActingPlayerId() {
    return fActingPlayerId;
  }

  public String getDefenderId() {
    return fDefenderId;
  }

  public boolean isUsingStab() {
    return fUsingStab;
  }

  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
    IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
    IJsonOption.USING_STAB.addTo(jsonObject, fUsingStab);
    return jsonObject;
  }
  
  public ClientCommandBlock initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(jsonObject);
    fDefenderId = IJsonOption.DEFENDER_ID.getFrom(jsonObject);
    fUsingStab = IJsonOption.USING_STAB.getFrom(jsonObject);
    return this;
  }

}
