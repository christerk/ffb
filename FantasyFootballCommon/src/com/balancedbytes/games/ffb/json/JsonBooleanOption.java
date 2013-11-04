package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonBooleanOption extends JsonAbstractOption {
  
  public JsonBooleanOption(String pKey) {
    super(pKey);
  }
  
  public boolean getFrom(JsonObject pJsonObject) {
    return getValueFrom(pJsonObject).asBoolean();
  }
  
  public void addTo(JsonObject pJsonObject, boolean pValue) {
    addValueTo(pJsonObject, JsonValue.valueOf(pValue));
  }
  
}
