package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
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

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 2;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getActingPlayerId());
    pByteList.addFieldCoordinate(getCoordinateFrom());
    FieldCoordinate[] coordinatesTo = getCoordinatesTo();
    pByteList.addByte((byte) coordinatesTo.length);
    for (FieldCoordinate coordinate : coordinatesTo) {
      pByteList.addFieldCoordinate(coordinate);
    }
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    if (byteArraySerializationVersion > 1) {
      fActingPlayerId = pByteArray.getString();
      fCoordinateFrom = pByteArray.getFieldCoordinate();
    }
    int nrOfCoordinates = pByteArray.getByte();
    for (int i = 0; i < nrOfCoordinates; i++) {
      addCoordinateTo(pByteArray.getFieldCoordinate());
    }
    return byteArraySerializationVersion;
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
