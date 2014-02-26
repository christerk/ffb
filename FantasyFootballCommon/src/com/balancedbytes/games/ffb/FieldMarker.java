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
public class FieldMarker implements IByteArrayReadable, IJsonSerializable {
  
  private FieldCoordinate fCoordinate;
  private String fHomeText;
  private String fAwayText;
  
  public FieldMarker() {
    super();
  }

  public FieldMarker(FieldCoordinate pCoordinate) {
    fCoordinate = pCoordinate;
  }
  
  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  public void setHomeText(String pHomeText) {
    fHomeText = pHomeText;
  }
  
  public String getHomeText() {
    return fHomeText;
  }
  
  public void setAwayText(String pAwayText) {
    fAwayText = pAwayText;
  }
  
  public String getAwayText() {
    return fAwayText;
  }
  
  public int hashCode() {
    return getCoordinate().hashCode();
  }
  
  public boolean equals(Object pObj) {
    return ((pObj instanceof FieldMarker) && getCoordinate().equals(((FieldMarker) pObj).getCoordinate()));
  }
  
  // Transformation
  
  public FieldMarker transform() {
    FieldMarker transformedMarker = new FieldMarker(getCoordinate().transform());
    transformedMarker.setAwayText(getHomeText());
    transformedMarker.setHomeText(getAwayText());
    return transformedMarker;
  }
  
  public static FieldMarker transform(FieldMarker pFieldMarker) {
    return (pFieldMarker != null) ? pFieldMarker.transform() : null;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addFieldCoordinate(getCoordinate());
    pByteList.addString(getHomeText());
    pByteList.addString(getAwayText());
  }
    
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fCoordinate = pByteArray.getFieldCoordinate();
    fHomeText = pByteArray.getString();
    fAwayText = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    IJsonOption.HOME_TEXT.addTo(jsonObject, fHomeText);
    IJsonOption.AWAY_TEXT.addTo(jsonObject, fAwayText);
    return jsonObject;
  }
  
  public FieldMarker initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
    fHomeText = IJsonOption.HOME_TEXT.getFrom(jsonObject);
    fAwayText = IJsonOption.AWAY_TEXT.getFrom(jsonObject);
    return this;
  }
  
}
