package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillPropertiesFactory;
import com.fumbbl.ffb.model.property.ISkillProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonSkillPropertiesMapOption extends JsonAbstractOption {
	public JsonSkillPropertiesMapOption(String pKey) {
		super(pKey);
	}

	public Map<String, Set<ISkillProperty>> getFrom(IFactorySource source, JsonObject jsonObject) {
		Map<String, Set<ISkillProperty>> map = new HashMap<>();

		if (isDefinedIn(jsonObject)) {
			SkillPropertiesFactory factory = source.getFactory(FactoryType.Factory.SKILL_PROPERTIES);
			JsonValue jsonValue = getValueFrom(jsonObject);
			if (jsonValue instanceof JsonObject) {
				JsonObject wrappedObject = (JsonObject) jsonValue;
				for (String name : wrappedObject.names()) {
					JsonValue arrayValue = wrappedObject.get(name);
					if (arrayValue instanceof JsonArray) {
						JsonArray array = (JsonArray) arrayValue;
						Set<ISkillProperty> properties = array.values().stream()
							.map(value -> factory.forName(value.asString())).collect(Collectors.toSet());
						map.put(name, properties);
					}
				}
			}

		}
		return map;
	}

	public void addTo(JsonObject pJsonObject, Map<String, Set<ISkillProperty>> map) {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, Set<ISkillProperty>> entry : map.entrySet()) {
			JsonArray array = new JsonArray();
			for (ISkillProperty property : entry.getValue()) {
				array.add(property.getName());
			}
			jsonObject.add(entry.getKey(), array);
		}

		addValueTo(pJsonObject, jsonObject);
	}
}
