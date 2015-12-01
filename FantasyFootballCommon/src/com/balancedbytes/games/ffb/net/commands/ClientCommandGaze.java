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
public class ClientCommandGaze extends NetCommand implements ICommandWithActingPlayer {
  
  private String fActingPlayerId;
  private String fVictimId;
  
  public ClientCommandGaze() {
    super();
  }

  public ClientCommandGaze(String pActingPlayerId, String pCatcherId) {
  	fActingPlayerId = pActingPlayerId;
    fVictimId = pCatcherId;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_GAZE;
  }
  
  public String getActingPlayerId() {
    return fActingPlayerId;
  }
  
  public String getVictimId() {
    return fVictimId;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
    IJsonOption.VICTIM_ID.addTo(jsonObject, fVictimId);
    return jsonObject;
  }
  
  public ClientCommandGaze initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(jsonObject);
    fVictimId = IJsonOption.VICTIM_ID.getFrom(jsonObject);
    return this;
  }
  
}
