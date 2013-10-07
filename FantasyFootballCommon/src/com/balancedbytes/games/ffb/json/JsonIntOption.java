package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonIntOption extends JsonAbstractOption {
  
  public JsonIntOption(String pKey) {
    super(pKey);
  }
  
  public int getFrom(JsonObject pJsonObject) {
    return getValueFrom(pJsonObject).asInt();
  }
  
  public int getFrom(JsonObject pJsonObject, int pDefault) {
    return getValueFrom(pJsonObject, JsonValue.valueOf(pDefault)).asInt();
  }

  public void addTo(JsonObject pJsonObject, int pValue) {
    addValueTo(pJsonObject, JsonValue.valueOf(pValue));
  }

}
