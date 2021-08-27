package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.TemporaryStatModifierFactory;
import com.fumbbl.ffb.modifiers.TemporaryStatModifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonTemporaryModifiersMapOption extends JsonAbstractOption {
	public JsonTemporaryModifiersMapOption(String pKey) {
		super(pKey);
	}

	public Map<String, Set<TemporaryStatModifier>> getFrom(IFactorySource source, JsonObject jsonObject) {
		Map<String, Set<TemporaryStatModifier>> map = new HashMap<>();

		if (isDefinedIn(jsonObject)) {
			TemporaryStatModifierFactory factory = source.getFactory(FactoryType.Factory.TEMPORARY_STAT_MODIFIER);
			JsonValue jsonValue = getValueFrom(jsonObject);
			if (jsonValue instanceof JsonObject) {
				JsonObject wrappedObject = (JsonObject) jsonValue;
				for (String name : wrappedObject.names()) {
					JsonValue arrayValue = wrappedObject.get(name);
					if (arrayValue instanceof JsonArray) {
						JsonArray array = (JsonArray) arrayValue;
						Set<TemporaryStatModifier> modifiers = array.values().stream()
							.map(value -> factory.forName(value.asString())).collect(Collectors.toSet());
						map.put(name, modifiers);
					}
				}
			}

		}
		return map;
	}

	public void addTo(JsonObject pJsonObject, Map<String, Set<TemporaryStatModifier>> map) {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, Set<TemporaryStatModifier>> entry : map.entrySet()) {
			JsonArray array = new JsonArray();
			for (TemporaryStatModifier modifier: entry.getValue()) {
				array.add(modifier.getName());
			}
			jsonObject.add(entry.getKey(), array);
		}

		addValueTo(pJsonObject, jsonObject);
	}
}
