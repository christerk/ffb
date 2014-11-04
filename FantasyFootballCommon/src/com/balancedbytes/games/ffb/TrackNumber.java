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
public class TrackNumber implements IByteArrayReadable, IJsonSerializable {
  
  private FieldCoordinate fCoordinate;
  private int fNumber;
  
  public TrackNumber() {
    super();
  }

  public TrackNumber(FieldCoordinate pCoordinate, int pNumber) {
    fCoordinate = pCoordinate;
    fNumber = pNumber;
  }
  
  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  public int getNumber() {
    return fNumber;
  }
  
  public int hashCode() {
    return getCoordinate().hashCode();
  }
  
  public boolean equals(Object pObj) {
    return (
      (pObj instanceof TrackNumber)
      && getCoordinate().equals(((TrackNumber) pObj).getCoordinate())
    );
  }
  
  public TrackNumber transform() {
    return new TrackNumber(getCoordinate().transform(), getNumber());
  }
  
  public static TrackNumber transform(TrackNumber pTrackNumber) {
    return (pTrackNumber != null) ? pTrackNumber.transform() : null;
  }
    
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  };
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getNumber());
    pByteList.addFieldCoordinate(getCoordinate());
  }
    
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fNumber = pByteArray.getByte();
    fCoordinate = pByteArray.getFieldCoordinate();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NUMBER.addTo(jsonObject, fNumber);
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    return jsonObject;
  }
  
  public TrackNumber initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fNumber = IJsonOption.NUMBER.getFrom(jsonObject);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
    return this;
  }
  
}
