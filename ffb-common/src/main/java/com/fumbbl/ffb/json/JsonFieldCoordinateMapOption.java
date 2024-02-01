package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.IFactorySource;

import java.util.HashMap;
import java.util.Map;

public class JsonFieldCoordinateMapOption extends JsonAbstractOption {
	public JsonFieldCoordinateMapOption(String pKey) {
		super(pKey);
	}

	public Map<String, FieldCoordinate> getFrom(IFactorySource source, JsonObject jsonObject) {
		Map<String, FieldCoordinate> map = new HashMap<>();

		if (isDefinedIn(jsonObject)) {
			JsonValue jsonValue = getValueFrom(jsonObject);
			if (jsonValue instanceof JsonObject) {
				JsonObject wrappedObject = (JsonObject) jsonValue;
				for (String name : wrappedObject.names()) {
					JsonValue value = wrappedObject.get(name);
					map.put(name, UtilJson.toFieldCoordinate(value));
				}
			}

		}
		return map;
	}

	public void addTo(JsonObject pJsonObject, Map<String, FieldCoordinate> map) {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, FieldCoordinate> entry : map.entrySet()) {
			jsonObject.add(entry.getKey(), UtilJson.toJsonValue(entry.getValue()));
		}

		addValueTo(pJsonObject, jsonObject);
	}
}
