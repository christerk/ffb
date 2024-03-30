package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonIntegerListMapOption extends JsonAbstractOption {
	public JsonIntegerListMapOption(String pKey) {
		super(pKey);
	}

	public Map<String, List<Integer>> getFrom(IFactorySource source, JsonObject jsonObject) {
		Map<String, List<Integer>> map = new HashMap<>();

		if (isDefinedIn(jsonObject)) {
			JsonValue jsonValue = getValueFrom(jsonObject);
			if (jsonValue instanceof JsonObject) {
				JsonObject wrappedObject = (JsonObject) jsonValue;
				for (String name : wrappedObject.names()) {
					JsonValue wrappedValue = wrappedObject.get(name);
					if (wrappedValue instanceof JsonArray) {
						JsonArray wrappedArray = (JsonArray) wrappedValue;
						map.put(name, wrappedArray.values().stream().map(JsonValue::asInt).collect(Collectors.toList()));
					}
				}
			}
		}
		return map;
	}

	public void addTo(JsonObject pJsonObject, Map<String, List<Integer>> map) {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
			JsonArray jsonArray = new JsonArray();
			entry.getValue().forEach(jsonArray::add);
			jsonObject.add(entry.getKey(), jsonArray);
		}
		addValueTo(pJsonObject, jsonObject);
	}
}
