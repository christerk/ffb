package com.balancedbytes.games.ffb.json;

import java.util.Date;

import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;

/**
 * 
 * @author Kalimar
 */
public class JsonDateOption extends JsonAbstractOption {

	public JsonDateOption(String pKey) {
		super(pKey);
	}

	public Date getFrom(Game game, JsonObject pJsonObject) {
		return UtilJson.toDate(getValueFrom(pJsonObject));
	}

	public void addTo(JsonObject pJsonObject, Date pValue) {
		addValueTo(pJsonObject, UtilJson.toJsonValue(pValue));
	}

}
