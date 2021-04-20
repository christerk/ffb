package com.fumbbl.ffb.json;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;

/**
 * 
 * @author Kalimar
 */
public interface IJsonReadable {

	// will return "this"
	public Object initFrom(IFactorySource game, JsonValue pJsonValue);

}