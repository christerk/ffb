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
public final class PushbackSquare implements IByteArrayReadable, IJsonSerializable {

  private FieldCoordinate fCoordinate;
  private Direction fDirection;
  private boolean fSelected;
  private boolean fLocked;
  private boolean fHomeChoice;

  public PushbackSquare() {
    super();
  }
  
  public PushbackSquare(FieldCoordinate pCoordinate, Direction pDirection, boolean pHomeChoice) {
    if (pCoordinate == null) {
      throw new IllegalArgumentException("Parameter coordinate must not be null.");
    }
    fCoordinate = pCoordinate;
    fDirection = pDirection;
    fHomeChoice = pHomeChoice;
    fLocked = false;
  }
  
  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  public Direction getDirection() {
    return fDirection;
  }
  
  public boolean isSelected() {
    return fSelected;
  }
  
  public void setSelected(boolean pSelected) {
    fSelected = pSelected;
  }

  public void setLocked(boolean pLocked) {
    fLocked = pLocked;
  }

  public boolean isLocked() {
    return fLocked;
  }
  
  public void setHomeChoice(boolean pHomeChoice) {
    fHomeChoice = pHomeChoice;
  }
  
  public boolean isHomeChoice() {
    return fHomeChoice;
  }
    
  public PushbackSquare transform() {
    FieldCoordinate transformedCoordinate = getCoordinate().transform();
    Direction transformedDirection = new DirectionFactory().transform(getDirection());
    PushbackSquare transformedPushback = new PushbackSquare(transformedCoordinate, transformedDirection, !isHomeChoice());
    transformedPushback.setSelected(isSelected());
    transformedPushback.setLocked(isLocked());
    return transformedPushback;
  }
  
  public static PushbackSquare transform(PushbackSquare pPushbackSquare) {
    return (pPushbackSquare != null) ? pPushbackSquare.transform() : null;
  }
  
  public int hashCode() {
    return getCoordinate().hashCode();
  }
  
  public boolean equals(Object pObj) {
    return (
      (pObj instanceof PushbackSquare)
      && getCoordinate().equals(((PushbackSquare) pObj).getCoordinate())
    );
  }
  
  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addFieldCoordinate(getCoordinate());
    pByteList.addByte((byte) getDirection().getId());
    pByteList.addBoolean(isSelected());
    pByteList.addBoolean(isLocked());
    pByteList.addBoolean(isHomeChoice());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fCoordinate = pByteArray.getFieldCoordinate();
    fDirection = new DirectionFactory().forId(pByteArray.getByte());
    fSelected = pByteArray.getBoolean();
    fLocked = pByteArray.getBoolean();
    fHomeChoice = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    IJsonOption.DIRECTION.addTo(jsonObject, fDirection);
    IJsonOption.SELECTED.addTo(jsonObject, fSelected);
    IJsonOption.LOCKED.addTo(jsonObject, fLocked);
    IJsonOption.HOME_CHOICE.addTo(jsonObject, fHomeChoice);
    return jsonObject;
  }
  
  public PushbackSquare initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
    fDirection = (Direction) IJsonOption.DIRECTION.getFrom(jsonObject);
    fSelected = IJsonOption.SELECTED.getFrom(jsonObject);
    fLocked = IJsonOption.LOCKED.getFrom(jsonObject);
    fHomeChoice = IJsonOption.HOME_CHOICE.getFrom(jsonObject);
    return this;
  }
  
}
