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
public class BloodSpot implements IByteArrayReadable, IJsonSerializable {
  
  private PlayerState fInjury;
  private FieldCoordinate fCoordinate;
  private transient String fIconProperty;

  public BloodSpot() {
    super();
  }
  
  public BloodSpot(FieldCoordinate pCoordinate, PlayerState pInjury) {
    fInjury = pInjury;
    fCoordinate = pCoordinate;
  }
  
  public PlayerState getInjury() {
    return fInjury;
  }

  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  public void setIconProperty(String pIconProperty) {
    fIconProperty = pIconProperty;
  }
  
  public String getIconProperty() {
    return fIconProperty;
  }
  
  public BloodSpot transform() {
  	BloodSpot transformedBloodspot = new BloodSpot(getCoordinate().transform(), getInjury());
    transformedBloodspot.setIconProperty(getIconProperty());
    return transformedBloodspot;
  }
  
  public static BloodSpot transform(BloodSpot pBloodspot) {
    return (pBloodspot != null) ? pBloodspot.transform() : null;
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt((getInjury() != null) ? getInjury().getId() : 0);
    pByteList.addFieldCoordinate(getCoordinate());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fInjury = new PlayerState(pByteArray.getSmallInt());
    fCoordinate = pByteArray.getFieldCoordinate();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.INJURY.addTo(jsonObject, fInjury);
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    return jsonObject;
  }
  
  public BloodSpot initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fInjury = IJsonOption.INJURY.getFrom(jsonObject);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
    return this;
  }
      
}
