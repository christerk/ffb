package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.eclipsesource.json.JsonObject;

/**
 * 
 * @author Kalimar
 */
public class JsonFieldCoordinateOption extends JsonAbstractOption {
  
  public JsonFieldCoordinateOption(String pKey) {
    super(pKey);
  }
  
  public FieldCoordinate getFrom(JsonObject pJsonObject) {
    return UtilJson.asFieldCoordinate(getValueFrom(pJsonObject));
  }
  
  public FieldCoordinate getFrom(JsonObject pJsonObject, FieldCoordinate pDefault) {
    return UtilJson.asFieldCoordinate(getValueFrom(pJsonObject, UtilJson.asJsonValue(pDefault)));
  }

  public void addTo(JsonObject pJsonObject, FieldCoordinate pValue) {
    addValueTo(pJsonObject, UtilJson.asJsonValue(pValue));
  }

}
