package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * 
 * @author Kalimar
 */
public class JsonArrayOption extends JsonAbstractOption {
  
  public JsonArrayOption(String pKey) {
    super(pKey);
  }
  
  public JsonArray getFrom(JsonObject pJsonObject) {
    return getValueFrom(pJsonObject).asArray();
  }
  
  public void addTo(JsonObject pJsonObject, JsonArray pValue) {
    addValueTo(pJsonObject, pValue);
  }
  
}
