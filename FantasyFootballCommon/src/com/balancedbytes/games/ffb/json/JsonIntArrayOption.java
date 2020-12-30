package com.balancedbytes.games.ffb.json;

import java.util.Collection;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonIntArrayOption extends JsonAbstractOption {

	public JsonIntArrayOption(String pKey) {
		super(pKey);
	}

	public int[] getFrom(IFactorySource source, JsonObject pJsonObject) {
		JsonValue value = getValueFrom(pJsonObject);
		if ((value != null) && !value.isNull()) {
			return toIntArray(value.asArray());
		} else {
			return null;
		}
	}

	private int[] toIntArray(JsonArray pJsonArray) {
		if (pJsonArray == null) {
			return null;
		}
		int[] intArray = new int[pJsonArray.size()];
		for (int i = 0; i < intArray.length; i++) {
			intArray[i] = pJsonArray.get(i).asInt();
		}
		return intArray;
	}

	private JsonArray toJsonArray(int[] pIntArray) {
		if (pIntArray == null) {
			return null;
		}
		JsonArray jsonArray = new JsonArray();
		for (int i = 0; i < pIntArray.length; i++) {
			jsonArray.add(pIntArray[i]);
		}
		return jsonArray;
	}

	public void addTo(JsonObject pJsonObject, int[] pValues) {
		addValueTo(pJsonObject, toJsonArray(pValues));
	}

	public void addTo(JsonObject pJsonObject, Collection<Integer> pValues) {
		int[] intArray = null;
		if (pValues != null) {
			Integer[] integerArray = pValues.toArray(new Integer[pValues.size()]);
			intArray = new int[integerArray.length];
			for (int i = 0; i < intArray.length; i++) {
				intArray[i] = integerArray[i];
			}
		}
		addTo(pJsonObject, intArray);
	}

}
