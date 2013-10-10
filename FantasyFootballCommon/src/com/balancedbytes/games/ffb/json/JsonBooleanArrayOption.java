package com.balancedbytes.games.ffb.json;

import java.util.Collection;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * 
 * @author Kalimar
 */
public class JsonBooleanArrayOption extends JsonAbstractOption {
  
  public JsonBooleanArrayOption(String pKey) {
    super(pKey);
  }
  
  public boolean[] getFrom(JsonObject pJsonObject) {
    return toBooleanArray(getValueFrom(pJsonObject).asArray()); 
  }
  
  public boolean[] getFrom(JsonObject pJsonObject, boolean[] pDefaults) {
    return toBooleanArray(getValueFrom(pJsonObject, toJsonArray(pDefaults)).asArray());
  }
  
  private boolean[] toBooleanArray(JsonArray pJsonArray) {
    if (pJsonArray == null) {
      return null;
    }
    boolean[] booleanArray = new boolean[pJsonArray.size()];
    for (int i = 0; i < booleanArray.length; i++) {
      booleanArray[i] = pJsonArray.get(i).asBoolean();
    }
    return booleanArray;
  }
  
  private JsonArray toJsonArray(boolean[] pBooleanArray) {
    if (pBooleanArray == null) {
      return null;
    }
    JsonArray jsonArray = new JsonArray();
    for (int i = 0; i < pBooleanArray.length; i++) {
      jsonArray.add(pBooleanArray[i]);
    }
    return jsonArray;
  }

  public void addTo(JsonObject pJsonObject, boolean[] pValues) {
    addValueTo(pJsonObject, toJsonArray(pValues));
  }

  public void addTo(JsonObject pJsonObject, Collection<Boolean> pValues) {
    boolean[] booleanArray = null;
    if (pValues != null) {
      Boolean[] booleanObjectArray = pValues.toArray(new Boolean[pValues.size()]);
      booleanArray = new boolean[booleanObjectArray.length];
      for (int i = 0; i < booleanArray.length; i++) {
        booleanArray[i] = booleanObjectArray[i];
      }
    }
    addTo(pJsonObject, booleanArray);
  }

}
