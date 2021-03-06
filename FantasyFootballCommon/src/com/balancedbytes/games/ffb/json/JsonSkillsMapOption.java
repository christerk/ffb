package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.model.Skill;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonSkillsMapOption extends JsonAbstractOption {
	public JsonSkillsMapOption(String pKey) {
		super(pKey);
	}

	public Map<String, Set<Skill>> getFrom(IFactorySource source, JsonObject jsonObject) {
		Map<String, Set<Skill>> map = new HashMap<>();

		if (isDefinedIn(jsonObject)) {
			SkillFactory factory = source.getFactory(FactoryType.Factory.SKILL);
			JsonValue jsonValue = getValueFrom(jsonObject);
			if (jsonValue instanceof JsonObject) {
				JsonObject wrappedObject = (JsonObject) jsonValue;
				for (String name : wrappedObject.names()) {
					JsonValue arrayValue = wrappedObject.get(name);
					if (arrayValue instanceof JsonArray) {
						JsonArray array = (JsonArray) arrayValue;
						Set<Skill> skills = array.values().stream()
							.map(value -> factory.forName(value.asString())).collect(Collectors.toSet());
						map.put(name, skills);
					}
				}
			}

		}
		return map;
	}

	public void addTo(JsonObject pJsonObject, Map<String, Set<Skill>> map) {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, Set<Skill>> entry : map.entrySet()) {
			JsonArray array = new JsonArray();
			for (Skill skill: entry.getValue()) {
				array.add(skill.getName());
			}
			jsonObject.add(entry.getKey(), array);
		}

		addValueTo(pJsonObject, jsonObject);
	}
}
