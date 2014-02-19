package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.UtilNetCommand;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandDeleteGame extends InternalServerCommand {
  
  private boolean fWithGamesInfo;
  
  public InternalServerCommandDeleteGame(long pGameId, boolean pWithGamesInfo) {
    super(pGameId);
    fWithGamesInfo = pWithGamesInfo;
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_DELETE_GAME;
  }
  
  public boolean isWithGamesInfo() {
    return fWithGamesInfo;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.WITH_GAMES_INFO.addTo(jsonObject, fWithGamesInfo);
    return jsonObject;
  }

  public InternalServerCommandDeleteGame initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fWithGamesInfo = IServerJsonOption.WITH_GAMES_INFO.getFrom(jsonObject);
    return this;
  }

}
