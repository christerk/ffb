package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonStringMapListOption extends JsonAbstractOption {
	public JsonStringMapListOption(String pKey) {
		super(pKey);
	}

	public List<Map<String, String>> getFrom(IFactorySource ignoredSource, JsonObject jsonObject) {
		List<Map<String, String>> list = new ArrayList<>();

		if (isDefinedIn(jsonObject)) {
			JsonValue jsonValue = getValueFrom(jsonObject);
			if (jsonValue instanceof JsonArray) {
				JsonArray array = (JsonArray) jsonValue;
				for (JsonValue value : array.values()) {
					Map<String, String> map = new HashMap<>();
					list.add(map);
					JsonObject object = UtilJson.toJsonObject(value);
					for (String name : object.names()) {
						map.put(name, object.getString(name, null));
					}
				}

			}
		}
		return list;
	}

	public void addTo(JsonObject pJsonObject, List<Map<String, String>> list) {
		JsonArray array = new JsonArray();
		for (Map<String, String> element : list) {
			JsonObject map = new JsonObject();
			for (String name: element.keySet()) {
				map.add(name, element.get(name));
			}
			array.add(map);
		}
		addValueTo(pJsonObject, array);
	}
}
