package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.util.StringTool;

import java.util.HashMap;
import java.util.Map;

public class JsonStringMapOption extends JsonAbstractOption {
	public JsonStringMapOption(String pKey) {
		super(pKey);
	}

	public Map<String, String> getFrom(IFactorySource source, JsonObject jsonObject) {
		Map<String, String> map = new HashMap<>();

		if (isDefinedIn(jsonObject)) {
			JsonValue jsonValue = getValueFrom(jsonObject);
			if (jsonValue instanceof JsonObject) {
				JsonObject wrappedObject = (JsonObject) jsonValue;
				for (String name : wrappedObject.names()) {
					String value = new JsonStringOption(name).getFrom(source, wrappedObject);
					if (StringTool.isProvided(value)) {
						map.put(name, value);
					} else {
						map.put(name, null);
					}
				}
			}

		}
		return map;
	}

	public void addTo(JsonObject pJsonObject, Map<String, String> map) {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			new JsonStringOption(entry.getKey()).addTo(jsonObject, entry.getValue());
		}
		addValueTo(pJsonObject, jsonObject);
	}
}
