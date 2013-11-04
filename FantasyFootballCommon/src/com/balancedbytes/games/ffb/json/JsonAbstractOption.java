package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public abstract class JsonAbstractOption {
  
  private String fKey;
  
  public JsonAbstractOption(String pKey) {
    if ((pKey == null) || (pKey.length() == 0)) {
      throw new IllegalArgumentException("Parameter key must not be null or empty.");
    }
    fKey = pKey;
  }
  
  public String getKey() {
    return fKey;
  }
  
  protected JsonValue getValueFrom(JsonObject pJsonObject) {
    if (pJsonObject == null) {
      throwJsonObjectIsNullException();
    }
    return pJsonObject.get(getKey());
  }

  protected void addValueTo(JsonObject pJsonObject, JsonValue pValue) {
    if (pJsonObject == null) {
      throwJsonObjectIsNullException();
    }
    pJsonObject.add(getKey(), pValue);
  }
  
  private void throwJsonObjectIsNullException() {
    throw new IllegalArgumentException("Parameter jsonObject must not be null.");
  }

}
