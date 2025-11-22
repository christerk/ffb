package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public abstract class JsonAbstractOption {

	private final String fKey;

	public JsonAbstractOption(String pKey) {
		if ((pKey == null) || (pKey.isEmpty())) {
			throw new IllegalArgumentException("Parameter key must not be null or empty.");
		}
		fKey = pKey;
	}

	public String getKey() {
		return fKey;
	}

	public boolean isDefinedIn(JsonObject pJsonObject) {
		return (getValueFrom(pJsonObject) != null);
	}

	protected JsonValue getValueFrom(JsonObject pJsonObject) {
		if (pJsonObject == null) {
			throwJsonObjectIsNullException();
		}
		return pJsonObject.get(getKey());
	}

	protected void addValueTo(JsonObject pJsonObject, JsonValue pValue) {
		if (pJsonObject == null) {
			throwJsonObjectIsNullException();
		}
		if (pValue != null) {
			pJsonObject.add(getKey(), pValue);
		} else {
			//noinspection deprecation
			pJsonObject.add(getKey(), JsonValue.NULL);
		}
	}

	private void throwJsonObjectIsNullException() {
		throw new IllegalArgumentException("Parameter jsonObject must not be null.");
	}

}
