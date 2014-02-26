package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class Pushback implements IByteArrayReadable, IJsonSerializable {
  
  private String fPlayerId;
  private FieldCoordinate fCoordinate;
  
  public Pushback() {
    super();
  }
  
  public Pushback(String pPlayerId, FieldCoordinate pCoordinate) {
    fPlayerId = pPlayerId;
    fCoordinate = pCoordinate;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  // Transformation
  
  public Pushback transform() {
    return new Pushback(getPlayerId(), FieldCoordinate.transform(getCoordinate()));
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addFieldCoordinate(getCoordinate());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fCoordinate = pByteArray.getFieldCoordinate();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    return jsonObject;
  }
  
  public Pushback initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
    return this;
  }

}
