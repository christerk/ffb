package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class Inducement implements IJsonSerializable {
  
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
