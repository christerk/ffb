package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonStringListMapOption extends JsonAbstractOption {
	public JsonStringListMapOption(String pKey) {
		super(pKey);
	}

	public Map<String, List<String>> getFrom(IFactorySource source, JsonObject jsonObject) {
		Map<String, List<String>> map = new HashMap<>();

		if (isDefinedIn(jsonObject)) {
			JsonValue jsonValue = getValueFrom(jsonObject);
			if (jsonValue instanceof JsonObject) {
				JsonObject wrappedObject = (JsonObject) jsonValue;
				for (String name : wrappedObject.names()) {
					String[] value = new JsonStringArrayOption(name).getFrom(source, wrappedObject);
					if (ArrayTool.isProvided(value)) {
						map.put(name, Arrays.asList(value));
					} else {
						map.put(name, null);
					}
				}
			}

		}
		return map;
	}

	public void addTo(JsonObject pJsonObject, Map<String, List<String>> map) {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			new JsonStringArrayOption(entry.getKey()).addTo(jsonObject, entry.getValue());
		}
		addValueTo(pJsonObject, jsonObject);
	}
}
