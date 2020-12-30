package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonArrayOption extends JsonAbstractOption {

	public JsonArrayOption(String pKey) {
		super(pKey);
	}

	public JsonArray getFrom(IFactorySource source, JsonObject pJsonObject) {
		JsonValue jsonValue = getValueFrom(pJsonObject);
		if ((jsonValue == null) || jsonValue.isNull()) {
			return new JsonArray();
		}
		return jsonValue.asArray();
	}

	public void addTo(JsonObject pJsonObject, JsonArray pValue) {
		addValueTo(pJsonObject, pValue);
	}

}
