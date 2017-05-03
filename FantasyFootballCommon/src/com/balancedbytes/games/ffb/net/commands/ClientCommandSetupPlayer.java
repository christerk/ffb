package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class ClientCommandSetupPlayer extends ClientCommand {
  
  private String fPlayerId;
  private FieldCoordinate fCoordinate;
  
  public ClientCommandSetupPlayer() {
    super();
  }

  public ClientCommandSetupPlayer(String pPlayerId, FieldCoordinate pCoordinate) {
    fPlayerId = pPlayerId;
    fCoordinate = pCoordinate;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_SETUP_PLAYER;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    return jsonObject;
  }
  
  public ClientCommandSetupPlayer initFrom(JsonValue jsonValue) {
    super.initFrom(jsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
    return this;
  }
      
}
