package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.INamedObjectFactory;

/**
 * 
 * @author Kalimar
 */
public class JsonEnumWithNameOption extends JsonAbstractOption {
	private Factory factory;
	
	public JsonEnumWithNameOption(String pKey, Factory factory) {
		super(pKey);
		
		this.factory = factory;
	}

	public INamedObject getFrom(IFactorySource source, JsonObject pJsonObject) {
		return asEnumWithName(source, getValueFrom(pJsonObject));
	}

	public void addTo(JsonObject pJsonObject, INamedObject pValue) {
		addValueTo(pJsonObject, asJsonValue(pValue));
	}

	private INamedObject asEnumWithName(IFactorySource source, JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		return getFactory(source).forName(pJsonValue.asString());
	}

	private JsonValue asJsonValue(INamedObject pEnumWithName) {
		if (pEnumWithName == null) {
			return JsonValue.NULL;
		}
		return JsonValue.valueOf(pEnumWithName.getName());
	}

	@SuppressWarnings("unchecked")
	private <T extends INamedObjectFactory> T getFactory(IFactorySource factorySource) {
		IFactorySource source = factorySource.forContext(factory.context);

		return (T) source.getFactory(factory);
	}
}
