package com.fumbbl.ffb.json;

import java.util.Date;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.factory.IFactorySource;

/**
 * 
 * @author Kalimar
 */
public class JsonDateOption extends JsonAbstractOption {

	public JsonDateOption(String pKey) {
		super(pKey);
	}

	public Date getFrom(IFactorySource source, JsonObject pJsonObject) {
		return UtilJson.toDate(getValueFrom(pJsonObject));
	}

	public void addTo(JsonObject pJsonObject, Date pValue) {
		addValueTo(pJsonObject, UtilJson.toJsonValue(pValue));
	}

}
