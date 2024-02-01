package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;

import java.util.HashMap;
import java.util.Map;

public class JsonIntegerMapOption extends JsonAbstractOption {
	public JsonIntegerMapOption(String pKey) {
		super(pKey);
	}

	public Map<String, Integer> getFrom(IFactorySource source, JsonObject jsonObject) {
		Map<String, Integer> map = new HashMap<>();

		if (isDefinedIn(jsonObject)) {
			JsonValue jsonValue = getValueFrom(jsonObject);
			if (jsonValue instanceof JsonObject) {
				JsonObject wrappedObject = (JsonObject) jsonValue;
				for (String name : wrappedObject.names()) {
					Integer value = wrappedObject.get(name).asInt();
					map.put(name, value);
				}
			}

		}
		return map;
	}

	public void addTo(JsonObject pJsonObject, Map<String, Integer> map) {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			jsonObject.add(entry.getKey(), entry.getValue());
		}

		addValueTo(pJsonObject, jsonObject);
	}
}
