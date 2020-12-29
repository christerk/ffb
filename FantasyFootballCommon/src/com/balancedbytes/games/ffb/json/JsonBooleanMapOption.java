package com.balancedbytes.games.ffb.json;

import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class JsonBooleanMapOption extends JsonAbstractOption {
	public JsonBooleanMapOption(String pKey) {
		super(pKey);
	}

	public Map<String, Boolean> getFrom(Game game, JsonObject jsonObject) {
		Map<String, Boolean> map = new HashMap<String, Boolean>();

		if (isDefinedIn(jsonObject)) {
			JsonValue jsonValue = getValueFrom(jsonObject);
			if (jsonValue instanceof JsonObject) {
				JsonObject wrappedObject = (JsonObject) jsonValue;
				for (String name : wrappedObject.names()) {
					Boolean value = wrappedObject.get(name).asBoolean();
					map.put(name, value);
				}
			}

		}
		return map;
	}

	public void addTo(JsonObject pJsonObject, Map<String, Boolean> map) {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, Boolean> entry : map.entrySet()) {
			jsonObject.add(entry.getKey(), entry.getValue());
		}

		addValueTo(pJsonObject, jsonObject);
	}
}
