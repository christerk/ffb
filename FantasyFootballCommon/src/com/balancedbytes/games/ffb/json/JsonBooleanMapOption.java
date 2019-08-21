package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class JsonBooleanMapOption extends JsonAbstractOption {
  public JsonBooleanMapOption(String pKey) {
    super(pKey);
  }

  public Map<String, Boolean> getFrom(JsonObject jsonObject) {
    Map<String, Boolean> map = new HashMap<>();
    for (String name: jsonObject.names()) {
      Boolean value = jsonObject.get(name).asBoolean();
      map.put(name, value);
    }
    return map;
  }

  public void addTo(JsonObject pJsonObject, Map<String, Boolean> map) {
    JsonObject jsonObject = new JsonObject();
    for (Map.Entry<String, Boolean> entry: map.entrySet()) {
      jsonObject.add(entry.getKey(), entry.getValue());
    }

    addValueTo(pJsonObject, jsonObject);
  }
}
