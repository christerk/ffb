package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public interface IJsonReadable {

	// will return "this"
	public Object initFrom(IFactorySource game, JsonValue pJsonValue);

}