package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.INamedObjectFactory;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonEnumWithNameOption extends JsonAbstractOption {

	private INamedObjectFactory fFactory;

	public JsonEnumWithNameOption(String pKey, INamedObjectFactory pFactory) {
		super(pKey);
		fFactory = pFactory;
		if (fFactory == null) {
			throw new IllegalArgumentException("Parameter factory must not be null.");
		}
	}

	public INamedObject getFrom(JsonObject pJsonObject) {
		return asEnumWithName(getValueFrom(pJsonObject));
	}

	public void addTo(JsonObject pJsonObject, INamedObject pValue) {
		addValueTo(pJsonObject, asJsonValue(pValue));
	}

	private INamedObject asEnumWithName(JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		return fFactory.forName(pJsonValue.asString());
	}

	private JsonValue asJsonValue(INamedObject pEnumWithName) {
		if (pEnumWithName == null) {
			return JsonValue.NULL;
		}
		return JsonValue.valueOf(pEnumWithName.getName());
	}

}
