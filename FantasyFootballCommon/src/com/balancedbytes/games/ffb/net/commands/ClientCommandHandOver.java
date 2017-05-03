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
public class ClientCommandHandOver extends ClientCommand implements ICommandWithActingPlayer {
  
  private String fActingPlayerId;
  private String fCatcherId;
  
  public ClientCommandHandOver() {
    super();
  }

  public ClientCommandHandOver(String pActingPlayerId, String pCatcherId) {
  	fActingPlayerId = pActingPlayerId;
    fCatcherId = pCatcherId;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_HAND_OVER;
  }
  
  public String getActingPlayerId() {
    return fActingPlayerId;
  }
  
  public String getCatcherId() {
    return fCatcherId;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
    IJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
    return jsonObject;
  }
  
  public ClientCommandHandOver initFrom(JsonValue jsonValue) {
    super.initFrom(jsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
    fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(jsonObject);
    fCatcherId = IJsonOption.CATCHER_ID.getFrom(jsonObject);
    return this;
  }
    
}
