package com.balancedbytes.games.ffb.json;

import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public interface IJsonReadable {

	// will return "this"
	public Object initFrom(JsonValue pJsonValue);

}