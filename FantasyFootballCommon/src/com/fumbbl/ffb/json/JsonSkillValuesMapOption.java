package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.skill.Skill;

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
