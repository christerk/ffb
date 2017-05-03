package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandFoul extends ClientCommand implements ICommandWithActingPlayer {

  private String fActingPlayerId;
  private String fDefenderId;

  public ClientCommandFoul() {
    super();
  }

  public ClientCommandFoul(String pActingPlayerId, String pDefenderId) {
    fActingPlayerId = pActingPlayerId;
    fDefenderId = pDefenderId;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_FOUL;
  }

  public String getActingPlayerId() {
    return fActingPlayerId;
  }

  public String getDefenderId() {
    return fDefenderId;
  }

  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
    IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
    return jsonObject;
  }
  
  public ClientCommandFoul initFrom(JsonValue jsonValue) {
    super.initFrom(jsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
    fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(jsonObject);
    fDefenderId = IJsonOption.DEFENDER_ID.getFrom(jsonObject);
    return this;
  }

}
