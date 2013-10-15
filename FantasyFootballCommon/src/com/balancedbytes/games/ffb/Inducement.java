package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class Inducement implements IByteArraySerializable, IJsonSerializable {
  
  private InducementType fType;
  private int fValue;
  private int fUses;

  public Inducement() {
    super();
  }
  
  public Inducement(InducementType pType, int pValue) {
    fType = pType;
    setValue(pValue);
  }

  public InducementType getType() {
    return fType;
  }
  
  public int getValue() {
    return fValue;
  }
  
  public void setValue(int pValue) {
    fValue = pValue;
  }
  
  public int getUses() {
    return fUses;
  }
  
  public void setUses(int pCurrent) {
    fUses = pCurrent;
  }
  
  public int getUsesLeft() {
    return Math.max(0, getValue() - getUses());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 2;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getType() != null) ? getType().getId() : 0)); 
    pByteList.addByte((byte) getValue()); 
    pByteList.addByte((byte) getUses());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fType = new InducementTypeFactory().forId(pByteArray.getByte());
    fValue = pByteArray.getByte();
    fUses = pByteArray.getByte();
    if (byteArraySerializationVersion < 2) {
    	pByteArray.getByte();  // modifier is deprecated
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.INDUCEMENT_TYPE.addTo(jsonObject, fType);
    IJsonOption.VALUE.addTo(jsonObject, fValue);
    IJsonOption.USES.addTo(jsonObject, fUses);
    return jsonObject;
  }
  
  public Inducement initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fType = (InducementType) IJsonOption.INDUCEMENT_TYPE.getFrom(jsonObject);
    fValue = IJsonOption.VALUE.getFrom(jsonObject);
    fUses = IJsonOption.USES.getFrom(jsonObject);
    return this;
  }
  
}
