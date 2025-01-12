package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.IFactorySource;

/**
 * 
 * @author Kalimar
 */
public class JsonPlayerStateOption extends JsonAbstractOption {

	public JsonPlayerStateOption(String pKey) {
		super(pKey);
	}

	public PlayerState getFrom(IFactorySource ignoredGame, JsonObject pJsonObject) {
		return UtilJson.toPlayerState(getValueFrom(pJsonObject));
	}

	public void addTo(JsonObject pJsonObject, PlayerState pValue) {
		addValueTo(pJsonObject, UtilJson.toJsonValue(pValue));
	}

}
