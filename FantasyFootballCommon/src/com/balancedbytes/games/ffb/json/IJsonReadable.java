package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public interface IJsonReadable {

	// will return "this"
	public Object initFrom(Game game, JsonValue pJsonValue);

}