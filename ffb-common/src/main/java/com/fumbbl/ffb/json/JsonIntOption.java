package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;

/**
 * 
 * @author Kalimar
 */
public class JsonIntOption extends JsonAbstractOption {

	public JsonIntOption(String pKey) {
		super(pKey);
	}

	public int getFrom(IFactorySource source, JsonObject pJsonObject) {
		JsonValue value = getValueFrom(pJsonObject);
		if ((value == null) || value.isNull()) {
			return 0;
		}
		return value.asInt();
	}

	public void addTo(JsonObject pJsonObject, int pValue) {
		addValueTo(pJsonObject, JsonValue.valueOf(pValue));
	}

}
