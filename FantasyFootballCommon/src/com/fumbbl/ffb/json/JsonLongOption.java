package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;

/**
 * 
 * @author Kalimar
 */
public class JsonLongOption extends JsonAbstractOption {

	public JsonLongOption(String pKey) {
		super(pKey);
	}

	public long getFrom(IFactorySource source, JsonObject pJsonObject) {
		JsonValue value = getValueFrom(pJsonObject);
		if ((value == null) || value.isNull()) {
			return 0;
		}
		return value.asLong();
	}

	public void addTo(JsonObject pJsonObject, long pValue) {
		addValueTo(pJsonObject, JsonValue.valueOf(pValue));
	}

}
