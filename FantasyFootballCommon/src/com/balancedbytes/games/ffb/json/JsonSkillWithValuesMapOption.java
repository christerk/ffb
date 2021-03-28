package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.skill.SkillWithValue;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonSkillWithValuesMapOption extends JsonAbstractOption {
	public JsonSkillWithValuesMapOption(String pKey) {
		super(pKey);
	}

	public Map<String, Set<SkillWithValue>> getFrom(IFactorySource source, JsonObject jsonObject) {
		Map<String, Set<SkillWithValue>> map = new HashMap<>();

		if (isDefinedIn(jsonObject)) {
			SkillFactory factory = source.getFactory(FactoryType.Factory.SKILL);
			JsonValue jsonValue = getValueFrom(jsonObject);
			if (jsonValue instanceof JsonObject) {
				JsonObject wrappedObject = (JsonObject) jsonValue;
				for (String name : wrappedObject.names()) {
					JsonValue arrayValue = wrappedObject.get(name);
					if (arrayValue instanceof JsonArray) {
						JsonArray array = (JsonArray) arrayValue;
						Set<SkillWithValue> skills = array.values().stream()
							.map(value -> {
									String[] parts = value.asString().split("_");

									Skill skill = factory.forName(parts[0]);
									if (parts.length > 1) {
										return new SkillWithValue(skill, parts[1]);
									} else {
										return new SkillWithValue(skill);
									}
								}
							).collect(Collectors.toSet());
						map.put(name, skills);
					}
				}
			}

		}
		return map;
	}

	public void addTo(JsonObject pJsonObject, Map<String, Set<SkillWithValue>> map) {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, Set<SkillWithValue>> entry : map.entrySet()) {
			JsonArray array = new JsonArray();
			for (SkillWithValue skillWithValue : entry.getValue()) {
				String result = skillWithValue.getSkill().getName();
				if (skillWithValue.getValue().isPresent()) {
					result += "_" + skillWithValue.getValue().get();
				}
				array.add(result);
			}
			jsonObject.add(entry.getKey(), array);
		}

		addValueTo(pJsonObject, jsonObject);
	}
}
