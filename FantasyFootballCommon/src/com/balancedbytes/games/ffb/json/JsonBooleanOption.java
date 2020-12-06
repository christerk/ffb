package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonBooleanOption extends JsonAbstractOption {

	public JsonBooleanOption(String pKey) {
		super(pKey);
	}

	public Boolean getFrom(JsonObject pJsonObject) {
		if (isDefinedIn(pJsonObject)) {
			return getValueFrom(pJsonObject).asBoolean();
		}
		return null;
	}

	public void addTo(JsonObject pJsonObject, Boolean pValue) {
		if (pValue != null) {
			addValueTo(pJsonObject, JsonValue.valueOf(pValue));
		}
	}

}
