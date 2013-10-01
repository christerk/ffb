package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonFieldCoordinateOption extends JsonAbstractOption {
  
  public JsonFieldCoordinateOption(String pKey) {
    super(pKey);
  }
  
  public FieldCoordinate getFrom(JsonObject pJsonObject) {
    return asFieldCoordinate(getValueFrom(pJsonObject));
  }
  
  public FieldCoordinate getFrom(JsonObject pJsonObject, FieldCoordinate pDefault) {
    return asFieldCoordinate(getValueFrom(pJsonObject, asJsonValue(pDefault)));
  }

  public void addTo(JsonObject pJsonObject, FieldCoordinate pValue) {
    addValueTo(pJsonObject, asJsonValue(pValue));
  }

  private FieldCoordinate asFieldCoordinate(JsonValue pJsonValue) {
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    JsonArray jsonArray = pJsonValue.isArray() ? pJsonValue.asArray() : null;
    if ((jsonArray == null) || (jsonArray.size() != 2)) {
      throw new IllegalArgumentException("JsonValue is not a valid FieldCoordinate object.");
    }
    return new FieldCoordinate(jsonArray.get(0).asInt(), jsonArray.get(1).asInt());
  }
  
  private JsonValue asJsonValue(FieldCoordinate pFieldCoordinate) {
    if (pFieldCoordinate == null) {
      return JsonValue.NULL;
    }
    JsonArray jsonArray = new JsonArray();
    jsonArray.add(pFieldCoordinate.getX());
    jsonArray.add(pFieldCoordinate.getY());
    return jsonArray;
  }
  
}
