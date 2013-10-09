package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public interface IJsonSerializable {

  // will return "this"
  public Object initFrom(JsonValue pJsonValue);

  public JsonValue toJsonValue();

}