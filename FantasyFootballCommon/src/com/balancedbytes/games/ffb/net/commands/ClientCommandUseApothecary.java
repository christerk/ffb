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
public class ClientCommandUseApothecary extends ClientCommand {

  private String fPlayerId;
  private boolean fApothecaryUsed;

  public ClientCommandUseApothecary() {
    super();
  }

  public ClientCommandUseApothecary(String pPlayerId, boolean pApothecaryUsed) {
    fPlayerId = pPlayerId;
    fApothecaryUsed = pApothecaryUsed;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_USE_APOTHECARY;
  }

  public String getPlayerId() {
    return fPlayerId;
  }

  public boolean isApothecaryUsed() {
    return fApothecaryUsed;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.APOTHECARY_USED.addTo(jsonObject, fApothecaryUsed);
    return jsonObject;
  }

  public ClientCommandUseApothecary initFrom(JsonValue jsonValue) {
    super.initFrom(jsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fApothecaryUsed = IJsonOption.APOTHECARY_USED.getFrom(jsonObject);
    return this;
  }

}
