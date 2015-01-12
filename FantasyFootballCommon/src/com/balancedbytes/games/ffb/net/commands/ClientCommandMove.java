package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandMove extends NetCommand implements ICommandWithActingPlayer {

  private String fActingPlayerId;
  private FieldCoordinate fCoordinateFrom;
  private List<FieldCoordinate> fCoordinatesTo;

  public ClientCommandMove() {
    fCoordinatesTo = new ArrayList<FieldCoordinate>();
  }

  public ClientCommandMove(String pActingPlayerId, FieldCoordinate pCoordinateFrom, FieldCoordinate[] pCoordinatesTo) {
    this();
    fActingPlayerId = pActingPlayerId;
    fCoordinateFrom = pCoordinateFrom;
    addCoordinatesTo(pCoordinatesTo);
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_MOVE;
  }

  public String getActingPlayerId() {
    return fActingPlayerId;
  }

  private void addCoordinateTo(FieldCoordinate pCoordinateTo) {
    if (pCoordinateTo != null) {
      fCoordinatesTo.add(pCoordinateTo);
    }
  }

  private void addCoordinatesTo(FieldCoordinate[] pCoordinatesTo) {
    if (ArrayTool.isProvided(pCoordinatesTo)) {
      for (FieldCoordinate coordinate : pCoordinatesTo) {
        addCoordinateTo(coordinate);
      }
    }
  }

  public FieldCoordinate[] getCoordinatesTo() {
    return fCoordinatesTo.toArray(new FieldCoordinate[fCoordinatesTo.size()]);
  }

  public FieldCoordinate getCoordinateFrom() {
    return fCoordinateFrom;
  }

  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
    IJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
    IJsonOption.COORDINATES_TO.addTo(jsonObject, fCoordinatesTo);
    return jsonObject;
  }
  
  public ClientCommandMove initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(jsonObject);
    fCoordinateFrom = IJsonOption.COORDINATE_FROM.getFrom(jsonObject);
    addCoordinatesTo(IJsonOption.COORDINATES_TO.getFrom(jsonObject));
    return this;
  }

}
