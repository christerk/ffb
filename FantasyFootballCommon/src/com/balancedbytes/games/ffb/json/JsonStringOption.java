package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonStringOption extends JsonAbstractOption {

	public JsonStringOption(String pKey) {
		super(pKey);
	}

	public String getFrom(Game game, JsonObject pJsonObject) {
		return asString(getValueFrom(pJsonObject));
	}

	public void addTo(JsonObject pJsonObject, String pValue) {
		addValueTo(pJsonObject, JsonValue.valueOf(pValue));
	}

	private String asString(JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		return pJsonValue.asString();
	}

}
