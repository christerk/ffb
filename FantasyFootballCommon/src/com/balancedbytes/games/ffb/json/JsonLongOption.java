package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonLongOption extends JsonAbstractOption {
  
  public JsonLongOption(String pKey) {
    super(pKey);
  }
  
  public long getFrom(JsonObject pJsonObject) {
    return getValueFrom(pJsonObject).asLong();
  }
  
  public long getFrom(JsonObject pJsonObject, long pDefault) {
    return getValueFrom(pJsonObject, JsonValue.valueOf(pDefault)).asLong();
  }

  public void addTo(JsonObject pJsonObject, long pValue) {
    addValueTo(pJsonObject, JsonValue.valueOf(pValue));
  }
  
}
