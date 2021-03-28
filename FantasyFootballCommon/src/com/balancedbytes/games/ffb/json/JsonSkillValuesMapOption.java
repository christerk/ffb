package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class JsonSkillValuesMapOption extends JsonAbstractOption {
	public JsonSkillValuesMapOption(String pKey) {
		super(pKey);
	}

	public Map<Skill, String> getFrom(IFactorySource source, JsonObject jsonObject) {
		Map<Skill, String> map = new HashMap<>();

		if (isDefinedIn(jsonObject)) {
			SkillFactory factory = source.getFactory(FactoryType.Factory.SKILL);
			JsonValue jsonValue = getValueFrom(jsonObject);
			if (jsonValue instanceof JsonObject) {
				JsonObject wrappedObject = (JsonObject) jsonValue;
				for (String name : wrappedObject.names()) {
					Skill skill = factory.forName(name);
					JsonValue stringValue = wrappedObject.get(name);
					map.put(skill, stringValue.isNull() ? null :  stringValue.asString());
				}
			}
		}
		return map;
	}

	public void addTo(JsonObject pJsonObject, Map<Skill, String> map) {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<Skill, String> entry : map.entrySet()) {
			jsonObject.add(entry.getKey().getName(), entry.getValue());
		}

		addValueTo(pJsonObject, jsonObject);
	}
}
