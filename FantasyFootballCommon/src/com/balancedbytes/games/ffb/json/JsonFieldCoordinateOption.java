package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.eclipsesource.json.JsonObject;

/**
 * 
 * @author Kalimar
 */
public class JsonFieldCoordinateOption extends JsonAbstractOption {

	public JsonFieldCoordinateOption(String pKey) {
		super(pKey);
	}

	public FieldCoordinate getFrom(IFactorySource source, JsonObject pJsonObject) {
		return UtilJson.toFieldCoordinate(getValueFrom(pJsonObject));
	}

	public void addTo(JsonObject pJsonObject, FieldCoordinate pValue) {
		addValueTo(pJsonObject, UtilJson.toJsonValue(pValue));
	}

}
