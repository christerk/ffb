package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandGameState extends ServerCommand {
  
  private Game fGame;
  
  public ServerCommandGameState() {
    super();
  }
  
  public ServerCommandGameState(Game pGame) {
    this();
    fGame = pGame;
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_GAME_STATE;
  }
  
  public Game getGame() {
    return fGame;
  }
  
  public ServerCommandGameState transform() {
    return new ServerCommandGameState(getGame().transform());
  }
  
  public boolean isReplayable() {
    return false;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    if (fGame != null) {
      IJsonOption.GAME.addTo(jsonObject, fGame.toJsonValue());
    }
    return jsonObject;
  }
  
  public ServerCommandGameState initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
    JsonObject gameObject = IJsonOption.GAME.getFrom(jsonObject);
    if (gameObject != null) {
      fGame = new Game().initFrom(gameObject);
    }
    return this;
  }

}
