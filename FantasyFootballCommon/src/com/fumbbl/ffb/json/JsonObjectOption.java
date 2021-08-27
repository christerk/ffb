package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;

/**
 * 
 * @author Kalimar
 */
public class JsonObjectOption extends JsonAbstractOption {

	public JsonObjectOption(String pKey) {
		super(pKey);
	}

	public JsonObject getFrom(IFactorySource source, JsonObject pJsonObject) {
		JsonValue jsonValue = getValueFrom(pJsonObject);
		if ((jsonValue == null) || jsonValue.isNull()) {
			return null;
		} else {
			return jsonValue.asObject();
		}
	}

	public void addTo(JsonObject pJsonObject, JsonObject pValue) {
		if (pValue == null) {
			addValueTo(pJsonObject, JsonValue.NULL);
		} else {
			addValueTo(pJsonObject, pValue);
		}
	}

}
