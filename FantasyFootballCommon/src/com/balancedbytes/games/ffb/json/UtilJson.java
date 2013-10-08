package com.balancedbytes.games.ffb.json;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.IEnumWithName;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;
import com.balancedbytes.games.ffb.PlayerState;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class UtilJson {
  
  private static final DateFormat _TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // 2001-07-04T12:08:56.235
  
  public static JsonObject toJsonObject(JsonValue pJsonValue) {
    if ((pJsonValue == null) || !pJsonValue.isObject()) {
      throw new IllegalArgumentException("JsonValue is not an object.");
    }
    return pJsonValue.asObject();
  }

  public static JsonArray toJsonArray(JsonValue pJsonValue) {
    if ((pJsonValue == null) || !pJsonValue.isArray()) {
      throw new IllegalArgumentException("JsonValue is not an array.");
    }
    return pJsonValue.asArray();
  }
  
  public static FieldCoordinate toFieldCoordinate(JsonValue pJsonValue) {
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    JsonArray jsonArray = pJsonValue.isArray() ? pJsonValue.asArray() : null;
    if ((jsonArray == null) || (jsonArray.size() != 2)) {
      throw new IllegalArgumentException("JsonValue is not a valid FieldCoordinate object.");
    }
    return new FieldCoordinate(jsonArray.get(0).asInt(), jsonArray.get(1).asInt());
  }
  
  public static JsonValue toJsonValue(FieldCoordinate pFieldCoordinate) {
    if (pFieldCoordinate == null) {
      return JsonValue.NULL;
    }
    JsonArray jsonArray = new JsonArray();
    jsonArray.add(pFieldCoordinate.getX());
    jsonArray.add(pFieldCoordinate.getY());
    return jsonArray;
  }
  
  public static Date toDate(JsonValue pValue) {
    if (!pValue.isNull() && pValue.isString()) {
      try {
        return _TIMESTAMP_FORMAT.parse(pValue.asString());
      } catch (ParseException pParseException) {
        return null;
      }
    }
    return null;
  }

  public static JsonValue toJsonValue(Date pDate) {
    if (pDate != null) {
      return JsonValue.valueOf(_TIMESTAMP_FORMAT.format(pDate));
    }
    return JsonValue.NULL;
  }
  
  public static PlayerState toPlayerState(JsonValue pJsonValue) {
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    return new PlayerState(pJsonValue.asInt());
  }
  
  public static JsonValue toJsonValue(PlayerState pPlayerState) {
    if (pPlayerState == null) {
      return JsonValue.NULL;
    }
    return JsonValue.valueOf(pPlayerState.getId());
  }
  
  public static IEnumWithName toEnumWithName(IEnumWithNameFactory pFactory, JsonValue pJsonValue) {
    if (pFactory == null) {
      throw new IllegalArgumentException("Parameter factory must not be null.");
    }
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    return pFactory.forName(pJsonValue.asString());
  }

  public static JsonValue toJsonValue(IEnumWithName pEnumWithName) {
    if (pEnumWithName == null) {
      return JsonValue.NULL;
    }
    return JsonValue.valueOf(pEnumWithName.getName());
  }

}
