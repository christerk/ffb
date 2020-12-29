package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonIntOption extends JsonAbstractOption {

	public JsonIntOption(String pKey) {
		super(pKey);
	}

	public int getFrom(Game game, JsonObject pJsonObject) {
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
