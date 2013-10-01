package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class UtilJson {
  
  public static JsonObject asJsonObject(JsonValue pJsonValue) {
    if ((pJsonValue == null) || !pJsonValue.isObject()) {
      throw new IllegalArgumentException("JsonValue is not an object.");
    }
    return pJsonValue.asObject();
  }

  public static JsonArray asJsonArray(JsonValue pJsonValue) {
    if ((pJsonValue == null) || !pJsonValue.isArray()) {
      throw new IllegalArgumentException("JsonValue is not an array.");
    }
    return pJsonValue.asArray();
  }

}
