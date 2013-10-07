package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.FieldCoordinate;
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
  
  public static FieldCoordinate asFieldCoordinate(JsonValue pJsonValue) {
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    JsonArray jsonArray = pJsonValue.isArray() ? pJsonValue.asArray() : null;
    if ((jsonArray == null) || (jsonArray.size() != 2)) {
      throw new IllegalArgumentException("JsonValue is not a valid FieldCoordinate object.");
    }
    return new FieldCoordinate(jsonArray.get(0).asInt(), jsonArray.get(1).asInt());
  }
  
  public static JsonValue asJsonValue(FieldCoordinate pFieldCoordinate) {
    if (pFieldCoordinate == null) {
      return JsonValue.NULL;
    }
    JsonArray jsonArray = new JsonArray();
    jsonArray.add(pFieldCoordinate.getX());
    jsonArray.add(pFieldCoordinate.getY());
    return jsonArray;
  }

}
