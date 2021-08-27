package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.IFactorySource;

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
