package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public interface IJsonSerializable {
  
  public void initFrom(JsonValue pJsonValue);
  
  public JsonValue toJsonValue();

}
