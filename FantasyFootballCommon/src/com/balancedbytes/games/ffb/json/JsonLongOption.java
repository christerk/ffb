package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonLongOption extends JsonAbstractOption {

	public JsonLongOption(String pKey) {
		super(pKey);
	}

	public long getFrom(Game game, JsonObject pJsonObject) {
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
