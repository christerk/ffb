package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * 
 * @author Kalimar
 */
public class JsonStringArrayOption extends JsonAbstractOption {
  
  public JsonStringArrayOption(String pKey) {
    super(pKey);
  }
  
  public String[] getFrom(JsonObject pJsonObject) {
    return toStringArray(getValueFrom(pJsonObject).asArray()); 
  }
  
  public String[] getFrom(JsonObject pJsonObject, String[] pDefault) {
    return toStringArray(getValueFrom(pJsonObject, toJsonArray(pDefault)).asArray());
  }
  
  private String[] toStringArray(JsonArray pJsonArray) {
    if (pJsonArray == null) {
      return null;
    }
    String[] stringArray = new String[pJsonArray.size()];
    for (int i = 0; i < stringArray.length; i++) {
      stringArray[i] = pJsonArray.get(i).asString();
    }
    return stringArray;
  }
  
  private JsonArray toJsonArray(String[] pStringArray) {
    if (pStringArray == null) {
      return null;
    }
    JsonArray jsonArray = new JsonArray();
    for (int i = 0; i < pStringArray.length; i++) {
      jsonArray.add(pStringArray[i]);
    }
    return jsonArray;
  }

  public void addTo(JsonObject pJsonObject, String[] pValue) {
    addValueTo(pJsonObject, toJsonArray(pValue));
  }
  
}
