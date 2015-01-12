package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.FieldCoordinate;
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
public class ClientCommandTouchback extends NetCommand {

  private FieldCoordinate fBallCoordinate;

  public ClientCommandTouchback() {
    super();
  }

  public ClientCommandTouchback(FieldCoordinate pBallCoordinate) {
    fBallCoordinate = pBallCoordinate;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_TOUCHBACK;
  }

  public FieldCoordinate getBallCoordinate() {
    return fBallCoordinate;
  }

  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.BALL_COORDINATE.addTo(jsonObject, fBallCoordinate);
    return jsonObject;
  }
  
  public ClientCommandTouchback initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fBallCoordinate = IJsonOption.BALL_COORDINATE.getFrom(jsonObject);
    return this;
  }

}
