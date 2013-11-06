package com.balancedbytes.games.ffb.json;

import java.util.Collection;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonStringArrayOption extends JsonAbstractOption {
  
  public JsonStringArrayOption(String pKey) {
    super(pKey);
  }
  
  public String[] getFrom(JsonObject pJsonObject) {
    JsonValue value = getValueFrom(pJsonObject);
    if ((value != null) && !value.isNull()) {
      return toStringArray(value.asArray()); 
    } else {
      return null;
    }
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

  public void addTo(JsonObject pJsonObject, String[] pValues) {
    addValueTo(pJsonObject, toJsonArray(pValues));
  }
  
  public void addTo(JsonObject pJsonObject, Collection<String> pValues) {
    String[] stringArray = null;
    if (pValues != null) {
      stringArray = pValues.toArray(new String[pValues.size()]);
    }
    addTo(pJsonObject, stringArray);
  }
  
}
