package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonLegacySkillValuesOption extends JsonStringArrayOption {

	public JsonLegacySkillValuesOption(String pKey) {
		super(pKey);
	}

	@Override
	protected String[] toStringArray(JsonArray pJsonArray) {
		if (pJsonArray == null) {
			return null;
		}
		String[] stringArray = new String[pJsonArray.size()];
		for (int i = 0; i < stringArray.length; i++) {
			JsonValue jsonValue = pJsonArray.get(i);
			stringArray[i] = mapJsonValue(jsonValue);
		}
		return stringArray;
	}

	private String mapJsonValue(JsonValue jsonValue) {
		if (jsonValue == null || jsonValue.isNull()) {
			return null;
		}
		if (jsonValue.isNumber()) {
			int value = jsonValue.asInt();
			return value == 0 ? null : String.valueOf(value);
		}

		return jsonValue.asString();
	}
}
