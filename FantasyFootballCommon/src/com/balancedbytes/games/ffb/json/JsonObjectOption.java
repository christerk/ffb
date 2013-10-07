package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonObject;

/**
 * 
 * @author Kalimar
 */
public class JsonObjectOption extends JsonAbstractOption {
  
  public JsonObjectOption(String pKey) {
    super(pKey);
  }
  
  public JsonObject getFrom(JsonObject pJsonObject) {
    return getValueFrom(pJsonObject).asObject();
  }
  
  public JsonObject getFrom(JsonObject pJsonObject, JsonObject pDefault) {
    return getValueFrom(pJsonObject, pDefault).asObject();
  }

  public void addTo(JsonObject pJsonObject, JsonObject pValue) {
    addValueTo(pJsonObject, pValue);
  }
  
}
