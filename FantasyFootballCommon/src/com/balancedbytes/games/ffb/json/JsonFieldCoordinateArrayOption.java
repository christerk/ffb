package com.balancedbytes.games.ffb.json;

import java.util.Collection;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonFieldCoordinateArrayOption extends JsonAbstractOption {

	public JsonFieldCoordinateArrayOption(String pKey) {
		super(pKey);
	}

	public FieldCoordinate[] getFrom(IFactorySource source, JsonObject pJsonObject) {
		return asFieldCoordinates(getValueFrom(pJsonObject));
	}

	public void addTo(JsonObject pJsonObject, FieldCoordinate[] pValues) {
		addValueTo(pJsonObject, asJsonValue(pValues));
	}

	public void addTo(JsonObject pJsonObject, Collection<FieldCoordinate> pValues) {
		FieldCoordinate[] fieldCoordinateArray = null;
		if (pValues != null) {
			fieldCoordinateArray = pValues.toArray(new FieldCoordinate[pValues.size()]);
		}
		addTo(pJsonObject, fieldCoordinateArray);
	}

	private FieldCoordinate[] asFieldCoordinates(JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		JsonArray jsonArray = pJsonValue.isArray() ? pJsonValue.asArray() : null;
		if (jsonArray == null) {
			throw new IllegalArgumentException("JsonValue is not a valid FieldCoordinate array.");
		}
		FieldCoordinate[] fieldCoordinates = new FieldCoordinate[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			fieldCoordinates[i] = UtilJson.toFieldCoordinate(jsonArray.get(i));
		}
		return fieldCoordinates;
	}

	private JsonValue asJsonValue(FieldCoordinate[] pFieldCoordinates) {
		if (pFieldCoordinates == null) {
			return JsonValue.NULL;
		}
		JsonArray jsonArray = new JsonArray();
		for (int i = 0; i < pFieldCoordinates.length; i++) {
			jsonArray.add(UtilJson.toJsonValue(pFieldCoordinates[i]));
		}
		return jsonArray;
	}

}
