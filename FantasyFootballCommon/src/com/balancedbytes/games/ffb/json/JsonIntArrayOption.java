package com.balancedbytes.games.ffb.json;

import java.util.Collection;

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
  
  public int[] getFrom(JsonObject pJsonObject, int[] pDefaults) {
    return toIntArray(getValueFrom(pJsonObject, toJsonArray(pDefaults)).asArray());
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

  public void addTo(JsonObject pJsonObject, int[] pValues) {
    addValueTo(pJsonObject, toJsonArray(pValues));
  }

  public void addTo(JsonObject pJsonObject, Collection<Integer> pValues) {
    int[] intArray = null;
    if (pValues != null) {
      Integer[] integerArray = pValues.toArray(new Integer[pValues.size()]);
      intArray = new int[integerArray.length];
      for (int i = 0; i < intArray.length; i++) {
        intArray[i] = integerArray[i];
      }
    }
    addTo(pJsonObject, intArray);
  }

}
