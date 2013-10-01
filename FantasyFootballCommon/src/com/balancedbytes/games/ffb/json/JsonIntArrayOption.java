package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * 
 * @author Kalimar
 */
public class JsonIntArrayOption extends JsonAbstractOption {
  
  public JsonIntArrayOption(String pKey) {
    super(pKey);
  }
  
  public int[] getFrom(JsonObject pJsonObject) {
    return toIntArray(getValueFrom(pJsonObject).asArray()); 
  }
  
  public int[] getFrom(JsonObject pJsonObject, int[] pDefault) {
    return toIntArray(getValueFrom(pJsonObject, toJsonArray(pDefault)).asArray());
  }
  
  private int[] toIntArray(JsonArray pJsonArray) {
    if (pJsonArray == null) {
      return null;
    }
    int[] intArray = new int[pJsonArray.size()];
    for (int i = 0; i < intArray.length; i++) {
      intArray[i] = pJsonArray.get(i).asInt();
    }
    return intArray;
  }
  
  private JsonArray toJsonArray(int[] pIntArray) {
    if (pIntArray == null) {
      return null;
    }
    JsonArray jsonArray = new JsonArray();
    for (int i = 0; i < pIntArray.length; i++) {
      jsonArray.add(pIntArray[i]);
    }
    return jsonArray;
  }

  public void addTo(JsonObject pJsonObject, int[] pValue) {
    addValueTo(pJsonObject, toJsonArray(pValue));
  }
  
}
