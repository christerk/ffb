package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;

import java.util.Collection;

/**
 * 
 * @author Kalimar
 */
public class JsonStringArrayOption extends JsonAbstractOption {

	public JsonStringArrayOption(String pKey) {
		super(pKey);
	}

	public String[] getFrom(IFactorySource source, JsonObject pJsonObject) {
		JsonValue value = getValueFrom(pJsonObject);
		if ((value != null) && !value.isNull()) {
			return toStringArray(value.asArray());
		} else {
			return null;
		}
	}

	protected String[] toStringArray(JsonArray pJsonArray) {
		if (pJsonArray == null) {
			return null;
		}
		String[] stringArray = new String[pJsonArray.size()];
		for (int i = 0; i < stringArray.length; i++) {
			JsonValue jsonValue = pJsonArray.get(i);
			if ((jsonValue != null) && !jsonValue.isNull()) {
				stringArray[i] = jsonValue.asString();
			}
		}
		return stringArray;
	}

	private JsonArray toJsonArray(String[] pStringArray) {
		if (pStringArray == null) {
			return null;
		}
		JsonArray jsonArray = new JsonArray();
		for (int i = 0; i < pStringArray.length; i++) {
			jsonArray.add(pStringArray[i]);
		}
		return jsonArray;
	}

	public void addTo(JsonObject pJsonObject, String[] pValues) {
		addValueTo(pJsonObject, toJsonArray(pValues));
	}

	public void addTo(JsonObject pJsonObject, Collection<String> pValues) {
		String[] stringArray = null;
		if (pValues != null) {
			stringArray = pValues.toArray(new String[pValues.size()]);
		}
		addTo(pJsonObject, stringArray);
	}

}
