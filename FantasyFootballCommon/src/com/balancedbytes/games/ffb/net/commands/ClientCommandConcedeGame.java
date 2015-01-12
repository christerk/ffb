package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.ConcedeGameStatus;
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
public class ClientCommandConcedeGame extends NetCommand {
  
  private ConcedeGameStatus fConcedeGameStatus;
  
  public ClientCommandConcedeGame() {
    super();
  }

  public ClientCommandConcedeGame(ConcedeGameStatus pConcedeGameStatus) {
    fConcedeGameStatus = pConcedeGameStatus;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_CONCEDE_GAME;
  }
  
  public ConcedeGameStatus getConcedeGameStatus() {
    return fConcedeGameStatus;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.CONCEDE_GAME_STATUS.addTo(jsonObject, fConcedeGameStatus);
    return jsonObject;
  }
  
  public ClientCommandConcedeGame initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fConcedeGameStatus = (ConcedeGameStatus) IJsonOption.CONCEDE_GAME_STATUS.getFrom(jsonObject);
    return this;
  }
    
}
