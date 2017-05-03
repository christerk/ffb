package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandActingPlayer extends ClientCommand {

  private String fPlayerId;
  private PlayerAction fPlayerAction;
  private boolean fLeaping;

  public ClientCommandActingPlayer() {
    super();
  }

  public ClientCommandActingPlayer(String pPlayerId, PlayerAction pPlayerAction, boolean pLeaping) {
    fPlayerId = pPlayerId;
    fPlayerAction = pPlayerAction;
    fLeaping = pLeaping;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_ACTING_PLAYER;
  }

  public String getPlayerId() {
    return fPlayerId;
  }

  public PlayerAction getPlayerAction() {
    return fPlayerAction;
  }

  public boolean isLeaping() {
    return fLeaping;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.PLAYER_ACTION.addTo(jsonObject, fPlayerAction);
    IJsonOption.LEAPING.addTo(jsonObject, fLeaping);
    return jsonObject;
  }

  public ClientCommandActingPlayer initFrom(JsonValue jsonValue) {
    super.initFrom(jsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fPlayerAction = (PlayerAction) IJsonOption.PLAYER_ACTION.getFrom(jsonObject);
    fLeaping = IJsonOption.LEAPING.getFrom(jsonObject);
    return this;
  }

}
