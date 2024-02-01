package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;

/**
 * 
 * @author Kalimar
 */
public class JsonValueOption extends JsonAbstractOption {

	public JsonValueOption(String pKey) {
		super(pKey);
	}

	public JsonValue getFrom(IFactorySource source, JsonObject pJsonObject) {
		return getValueFrom(pJsonObject);
	}

	public void addTo(JsonObject pJsonObject, JsonValue pValue) {
		addValueTo(pJsonObject, pValue);
	}

}
